package org.cghr.commons.web.controller

import com.google.gson.Gson
import groovy.sql.Sql
import org.cghr.GenericGroovyContextLoader
import org.cghr.commons.db.DbAccess
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by ravitej on 5/5/14.
 */

@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyContextLoader.class)
class DataAccessSpec extends Specification {

    //@Autowired
    DataAccess dataAccess = new DataAccess()
    @Autowired
    HashMap dataStoreFactory
    @Autowired
    DbTester dt
    @Shared
    def dataSet
    @Autowired
    Sql gSql
    @Autowired
    DbAccess dbAccess


    MockMvc mockMvc

    def setupSpec() {
        dataSet = new MockData().sampleData.get('country')

    }

    def setup() {

        dt.cleanInsert('country')
        mockMvc = MockMvcBuilders.standaloneSetup(dataAccess).build()

        dataAccess.dbAccess = dbAccess
        dataAccess.dataStoreFactory=dataStoreFactory

    }

    def "should get data as a json for a given datastore,key and value"() {


        expect:
        mockMvc.perform(get("/data/dataAccessService/" + datastore + "/" + id + "/" + value))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(result))


        where:
        datastore | id   | value || result
        "country" | "id" | "1"    | new Gson().toJson(dataSet[0]).toString()
        "country" | "id" | "999"  | "{}"


    }

    def "should get resource as a json for a given entity and entityId"() {


        expect:
        mockMvc.perform(get("/data/dataAccessService/" + entity + "/" + entityId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(result))


        where:
        entity    | entityId || result
        "country" | "1"      || new Gson().toJson(dataSet[0]).toString()
        "country" | "999"    || "{}"


    }


}