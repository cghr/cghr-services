package org.cghr.commons.web.controller

import com.google.gson.Gson
import groovy.sql.Sql
import org.cghr.GenericGroovyContextLoader
import org.cghr.commons.db.DbStore
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class DataStoreBatchSpec extends Specification {

    @Autowired
    DataStoreBatch dataStoreBatch
    @Shared
    def dataChangelogs
    def data = [id: 1, name: 'india', continent: 'asia']
    @Autowired
    DbStore dbStore
    @Shared
    def countryBatchData


    @Shared
    def dataSet
    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

    MockMvc mockMvc

    def setupSpec() {
        dataSet = new MockData().sampleData.get("country")
        countryBatchData = new MockData().sampleData.get("countryBatchData")
    }

    def setup() {

        mockMvc = MockMvcBuilders.standaloneSetup(dataStoreBatch).build()
        dt.clean("country")
        dt.clean('datachangelog')
    }

    def "should save a map to database"() {
        setup:
        List array = [countryBatchData[0], countryBatchData[1], countryBatchData[2]]
        String json = new Gson().toJson(array)


        when:
        mockMvc.perform(post('/data/dataStoreBatchService').contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk()).andReturn();



        then:
        gSql.rows("select * from country").size() == 3
        gSql.rows("select * from datachangelog").size() == 3
    }
}
