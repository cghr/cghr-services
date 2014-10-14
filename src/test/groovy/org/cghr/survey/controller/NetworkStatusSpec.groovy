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
    NetworkStatusService networkStatus
    MockMvc mockMvc


    def "should get networkstatus as false for ipAddress pattern 192.168."() {
        given:
        mockMvc = MockMvcBuilders.standaloneSetup(networkStatus).build()
        expect:
        mockMvc.perform(get("/NetworkStatus"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string('{"status":false}'))

    }

    def "should get networkstatus as true for ipAddress pattern 127.0."() {
        given:
        networkStatus.pattern="127.0."
        mockMvc = MockMvcBuilders.standaloneSetup(networkStatus).build()

        expect:
        mockMvc.perform(get("/NetworkStatus"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string('{"status":true}'))

    }

}