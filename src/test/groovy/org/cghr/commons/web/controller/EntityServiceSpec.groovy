package org.cghr.commons.web.controller

import groovy.sql.Sql
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
import spock.lang.Subject

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by ravitej on 25/11/14.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class EntityServiceSpec extends Specification {

    @Autowired
    @Subject
    EntityService entityService

    MockMvc mockMvc
    @Autowired
    DbTester dbTester
    @Shared
    def dataSet

    @Autowired
    Sql gSql

    def setupSpec() {

        dataSet = new MockData().sampleData.get('country')
    }

    def setup() {

        dbTester.cleanInsert("country")
        mockMvc = MockMvcBuilders.standaloneSetup(entityService).build()

    }

    def "should respond with the entityData"() {

        expect:
        mockMvc.perform(get('/entity/country/1'))
                .andExpect(status().isOk())
                .andExpect(content().string(dataSet[0].toJson()))

        mockMvc.perform(get('/entity/country/2'))
                .andExpect(status().isOk())
                .andExpect(content().string(dataSet[1].toJson()))
    }

    def "should respond with empty  entity for invalid and unavailable entities"() {

        expect:
        mockMvc.perform(get('/entity/country/999'))
                .andExpect(status().isOk())
                .andExpect(content().string("{}"))

    }


    def "should respond with entityList"() {

        expect:
        mockMvc.perform(get('/entity/country'))
                .andExpect(status().isOk())
                .andExpect(content().string(dataSet.toJson()))

    }

    def "should respond with entityList with criteria"() {

        expect:
        mockMvc.perform(get('/entity/country/continent/asia'))
                .andExpect(status().isOk())
                .andExpect(content().string(dataSet.toJson()))

    }


    def "should save a new entity and log the entity"() {

        given:
        dbTester.clean('country,datachangelog')
        String json = dataSet[0].toJson()

        when:
        mockMvc.perform(post('/entity/country')
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andReturn();


        then:
        gSql.firstRow("select * from country where id=?", [1]) == dataSet[0]
        gSql.rows("select * from datachangelog").size() == 1

    }

    def "should save variant entities"() {

        given:
        dbTester.clean('country,datachangelog')
        List changelogs = [
                [datastore: 'country', data: dataSet[0]],
                [datastore: 'country', data: dataSet[1]],
                [datastore: 'country', data: dataSet[2]]
        ]

        when:
        mockMvc.perform(post('/entity')
                .contentType(MediaType.APPLICATION_JSON)
                .content(changelogs.toJson()))
                .andExpect(status().isOk())
                .andReturn();


        then:
        gSql.rows("select * from country") == dataSet
        gSql.firstRow("select count(*) count from datachangelog").count == 3

    }


    def "should delete a given entity"() {
        when:
        mockMvc.perform(delete('/entity/country/1'))
                .andExpect(status().isOk())

        then:
        gSql.firstRow("select * from country where id=?", [1]) == null

    }


}