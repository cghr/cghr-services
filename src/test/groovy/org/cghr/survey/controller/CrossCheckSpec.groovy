package org.cghr.survey.controller

import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
/**
 * Created by ravitej on 7/5/14.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class CrossCheckSpec extends Specification {

    @Autowired
    CrossCheckService crossCheckService
    @Autowired
    DbTester dbTester

    MockMvc mockMvc

    def setupSpec() {

    }

    def setup() {

        dbTester.cleanInsert('country')
        mockMvc=MockMvcBuilders.standaloneSetup(crossCheckService).build()
    }

    def "should get a value for a given cross check data"() {
        given:
        Map crossCheck=[entity:'country',field:'name',ref:'id',refId:'1']
        String json=(crossCheck).toJson()
        expect:
        mockMvc.perform(post('/survey/crossCheck').contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string('{"value":"india"}'))

    }

}