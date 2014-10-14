package org.cghr.survey.controller

import org.cghr.GenericGroovyContextLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by ravitej on 7/5/14.
 */
@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class NetworkStatusSpec extends Specification {

    @Autowired
    NetworkStatus networkStatus
    MockMvc mockMvc


    def setup() {

        mockMvc = MockMvcBuilders.standaloneSetup(networkStatus).build()
    }

    def "should get a value for a given cross check data"() {
        expect:
        mockMvc.perform(get("/NetworkStatus"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string('{"status":false}'))

    }

}