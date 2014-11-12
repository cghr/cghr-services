package org.cghr.dataSync.controller
import groovy.sql.Sql
import org.cghr.commons.db.DbAccess
import org.cghr.dataSync.service.SyncUtil
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
/**
 * Created by ravitej on 5/5/14.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class SyncIntegrationSpec extends Specification {

    @Autowired
    DbAccess dbAccess
    @Autowired
    Sql gSql
    @Autowired
    SyncService syncService
    @Autowired
    DbTester dbTester
    MockMvc mockMvc
    @Shared
    def dataSet

    def setupSpec() {

        dataSet = new MockData().sampleData.get('country')
    }

    def setup() {
        dbTester.clean('user,inbox,outbox,country,authtoken,filechangelog')
        dbTester.cleanInsert('datachangelog')

        mockMvc = MockMvcBuilders.standaloneSetup(syncService).build()

    }

    def "should download and upload data to a mock Server Online (Team Leader)"() {

        given:
        gSql.execute("insert into user(id,username,password,role) values(?,?,?,?)", [15, 'user1', 'password', 'manager'])
        //Make an entry in authtoken as Manager
        gSql.execute("insert into authtoken(id,token,username,role) values(?,?,?,?)", [1, "dummytoken", "user1", "manager"])

        when:
        mockMvc.perform(get('/sync/dataSync'))
                .andExpect(status().isOk())


        then:
        gSql.rows("select * from country").size() == 3
        gSql.rows("select * from datachangelog where status is null").size() == 0
        gSql.rows("select * from outbox").size() == 2


    }

    def "should download and upload data to a mock Server (Surveyor)"() {
        setup:
        Map[] downloadInfo = [[datastore: 'country', ref: 'continent', refId: 'asia', distList: null]]
        RestTemplate restTemplate = Stub() {
            getForObject('http://192.168.0.100:8080/app/status/manager', Map.class) >> [status: false]
            getForObject('http://192.168.0.101:8080/app/status/manager', Map.class) >> [status: true]
            getForObject('http://192.168.0.101:8080/app/api/sync/downloadInfo/15', List.class) >> downloadInfo
            getForObject('http://192.168.0.101:8080/app/api/data/dataAccessBatchService/country/continent/asia', List.class) >> dataSet
            postForLocation('http://192.168.0.101:8080/app/api/data/dataStoreBatchService', _) >> {}
        }
        String baseIp = '192.168.0.'
        Integer startNode = 100
        Integer endNode = 120
        Integer port = 8080

        String pathToCheck = 'status/manager'
        String appName = 'app'
        SyncUtil syncUtil = new SyncUtil(dbAccess, restTemplate, baseIp, startNode, endNode, port, pathToCheck, appName)
        syncService.syncRunner.agentProvider.agentServiceProvider.syncUtil = syncUtil
        syncService.syncRunner.agentProvider.agentServiceProvider.restTemplate = restTemplate

        gSql.execute("insert into user(id,username,password,role) values(?,?,?,?)", [15, 'user1', 'password', 'user'])
        //Make an entry in authtoken as User
        gSql.execute("insert into authtoken(id,token,username,role) values(?,?,?,?)", [1, "dummytoken", "user1", "user"])

        when:
        mockMvc.perform(get('/sync/dataSync'))
                .andExpect(status().isOk())

        then:
        gSql.rows("select * from country").size() == 3
        gSql.rows("select * from datachangelog where status is null").size() == 0


    }


}