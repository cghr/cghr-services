package org.cghr.dataSync.controller

import groovy.sql.Sql
import org.cghr.GenericGroovyContextLoader
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
/**
 * Created by ravitej on 6/5/14.
 */
@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class SyncStatusSpec extends Specification {

    @Autowired
    SyncStatus syncStatus
    MockMvc mockMvc
    @Autowired
    DbTester dbTester
    @Autowired
    Sql gSql

    def setupSpec() {

    }

    def setup() {

        dbTester.cleanInsert('datachangelog')
        dbTester.cleanInsert('inbox')
        dbTester.clean('authtoken')

        mockMvc = MockMvcBuilders.standaloneSetup(syncStatus).build()

    }

    def "should get the current status of download and upload"() {

        setup:
        gSql.execute("insert into authtoken(id,token,username,role) values(?,?,?,?)",[1,'faketoken','user2','manager'])


        expect:
        mockMvc.perform(get('/sync/status/download'))
        .andExpect(status().isOk())
        .andExpect(content().string('2'))

        mockMvc.perform(get('/sync/status/upload'))
                .andExpect(status().isOk())
                .andExpect(content().string('3'))

        mockMvc.perform(get('/sync/status/manager'))
                .andExpect(status().isOk())


    }


}