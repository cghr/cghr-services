package org.cghr.commons.web.controller
import com.google.gson.Gson
import org.cghr.commons.db.DbAccess
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class DataAccessBatchSpec extends Specification {

    @Autowired
    DataAccessBatch dataAccessBatch
    @Autowired
    DbAccess dbAccess
    @Autowired
    DbTester dbTester

    @Shared
    def dataSet
    MockMvc mockMvc

    def setupSpec() {

        dataSet = new MockData().sampleData.get("country")


    }

    def setup() {

        dbTester.cleanInsert('country')
        mockMvc = MockMvcBuilders.standaloneSetup(dataAccessBatch).build()
    }

    def "should get requested data as json"() {

        expect:
        mockMvc.perform(get("/data/dataAccessBatchService/" + datastore + "/" + keyField + "/" + keyFieldValue))
                .andExpect(status().isOk())
                .andExpect(content().string(result))

        where:
        datastore | keyField    | keyFieldValue || result
        "country" | "continent" | "asia"        || new Gson().toJson(dataSet).toString()
        "country" | "continent" | "antarctica"  || "[]"
    }
}