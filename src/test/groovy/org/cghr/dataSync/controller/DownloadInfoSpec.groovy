package org.cghr.dataSync.controller

import groovy.sql.Sql
import org.cghr.GenericGroovyContextLoader
import org.cghr.commons.db.DbAccess
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by ravitej on 5/5/14.
 */
@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class DownloadInfoSpec extends Specification {

    @Autowired
    DownloadInfo downloadInfo
    @Autowired
    DbAccess dbAccess
    @Autowired
    DbTester dbTester
    @Autowired
    Sql gSql

    MockMvc mockMvc

    def setupSpec() {


    }

    def setup() {

        mockMvc = MockMvcBuilders.standaloneSetup(downloadInfo).build()
        dbTester.cleanInsert('outbox')

    }

    def "should get downloadInfo as a json array"() {

        expect:
        mockMvc.perform(get('/sync/downloadInfo/15'))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string('[{"datastore":"country","ref":"id","refId":"1"}]'))

        gSql.rows("select * from outbox where dwnStatus=1 and recipient=15").size()==1


    }


}