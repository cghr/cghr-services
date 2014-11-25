package org.cghr.commons.web.controller

import com.google.gson.Gson
import groovy.sql.Sql
import org.cghr.commons.db.DbStore
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class DataStoreSpec extends Specification {

    @Autowired
    DataStore dataStore
    def data = [id: 1, name: 'india', continent: 'asia']


    @Shared
    def dataSet
    @Autowired
    Sql gSql
    @Autowired
    DbStore dbStore
    @Autowired
    DbTester dt
    MockMvc mockMvc

    def setupSpec() {
        dataSet = new MockData().sampleData.get("country")
    }

    def setup() {

        mockMvc = MockMvcBuilders.standaloneSetup(dataStore).build()

        dt.clean("country")
        dt.clean("datachangelog")
    }

    def "should save a map to database"() {

        given:
        String json = new Gson().toJson([id: 1, name: 'india', continent: 'asia', datastore: 'country'])

        when:
        mockMvc.perform(post('/data/dataStoreService')
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andReturn();


        then:
        gSql.firstRow("select * from country where id=?", [1]) == dataSet[0]
        gSql.rows("select * from datachangelog").size() == 1
    }
}
