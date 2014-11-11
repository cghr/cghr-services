package org.cghr.survey.controller

import org.cghr.GenericGroovyContextLoader
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by ravitej on 7/5/14.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyContextLoader.class)
class CrossFlowSpec extends Specification {

    @Autowired
    CrossFlowService crossFlowService
    @Autowired
    DbTester dbTester
    MockMvc mockMvc

    def setupSpec() {

    }

    def setup() {
        mockMvc=MockMvcBuilders.standaloneSetup(crossFlowService).build()
        dbTester.cleanInsert('country')

    }

    def "should perform cross flow on a given dataset"() {
        given:
        String crossFlows='[{"entity":"country","field":"name","ref":"id","refId":"1","condition":"name==\'india\'"}]'
        String failingCrossFlows='[{"entity":"country","field":"name","ref":"id","refId":"1","condition":"name==\'america\'"}]'
        expect:
        mockMvc.perform(post('/CrossFlowService').contentType(MediaType.APPLICATION_JSON).content(crossFlows))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string('{"check":true}'))

        mockMvc.perform(post('/CrossFlowService').contentType(MediaType.APPLICATION_JSON).content(failingCrossFlows))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string('{"check":false}'))



    }

}