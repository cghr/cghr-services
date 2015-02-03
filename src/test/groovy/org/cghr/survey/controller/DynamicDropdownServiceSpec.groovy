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
 * Created by ravitej on 3/2/15.
 */

@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class DynamicDropdownServiceSpec extends Specification {

    @Autowired
    DynamicDropdownService dynamicDropdownService
    @Autowired
    DbTester dbTester

    MockMvc mockMvc


    def setup() {
        dbTester.cleanInsert('country')
        mockMvc = MockMvcBuilders.standaloneSetup(dynamicDropdownService).build()
    }

    def "should respond with data for dynamic dropdown"() {

        given:
        Map metadata = [field: 'name', entity: 'country', ref: 'continent', refValue: 'asia']
        String json = metadata.toJson()
        List result = [
                [text: 'india', value: 'india'],
                [text: 'pakistan', value: 'pakistan'],
                [text: 'srilanka', value: 'srilanka']]

        expect:
        mockMvc.perform(post('/survey/dynamicDropdown').contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(result.toJson()))

    }
}
