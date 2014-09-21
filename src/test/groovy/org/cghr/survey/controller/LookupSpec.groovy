package org.cghr.survey.controller

import com.google.gson.Gson
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
@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class LookupSpec extends Specification {

    @Autowired
    LookupService lookupService
    @Autowired
    DbTester dbTester

    MockMvc mockMvc

    def setupSpec() {

    }

    def setup() {

        dbTester.cleanInsert('country')
        mockMvc=MockMvcBuilders.standaloneSetup(lookupService).build()
    }

    def "should get a value for a given lookup  data"() {
        given:
        Map lookup=[entity:'country',field:'name',ref:'continent',refId:'asia']
        String json=new Gson().toJson(lookup)
        expect:
        mockMvc.perform(post('/LookupService').contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string('[{"text":"india","value":"india"},{"text":"pakistan","value":"pakistan"},{"text":"srilanka","value":"srilanka"}]'))

    }
    def "should get a value for a given lookup  data with a condition"() {
        given:
        Map lookup=[entity:'country',field:'name',ref:'continent',refId:'asia',condition:'id>1']
        String json=new Gson().toJson(lookup)
        expect:
        mockMvc.perform(post('/LookupService').contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string('[{"text":"pakistan","value":"pakistan"},{"text":"srilanka","value":"srilanka"}]'))

    }


}