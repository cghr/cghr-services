package org.cghr.dataSync.controller
import groovy.sql.Sql
import org.cghr.GenericGroovyContextLoader
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Ignore
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
/**
 * Created by ravitej on 5/5/14.
 */
@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class SyncIntegrationSpec extends Specification {

    @Autowired
    Sql gSql
    @Autowired
    SyncService syncService
    @Autowired
    DbTester dbTester
    MockMvc mockMvc

    def setupSpec() {


    }

    def setup() {

        dbTester.clean('user')
        dbTester.clean('inbox')
        dbTester.clean('outbox')
        dbTester.clean('country')
        dbTester.clean('authtoken')

        dbTester.cleanInsert('datachangelog')

        mockMvc=MockMvcBuilders.standaloneSetup(syncService).build()

    }

    def "should download and upload data to a mock Server"() {

        given:
        gSql.execute("insert into user(id,username,password,role) values(?,?,?,?)", [15, 'user1', 'password', 'manager'])
        //Make an entry in authtoken as Manager
        gSql.execute("insert into authtoken(id,token,username,role) values(?,?,?,?)", [1, "fakeToken", "user1", "manager"])

        when:
        mockMvc.perform(get('/sync/dataSync'))
        .andExpect(status().isOk())


        then:
        gSql.rows("select * from country").size() == 3
        gSql.rows("select * from datachangelog where status is null").size() == 0
        gSql.rows("select * from outbox").size() == 2


    }


}