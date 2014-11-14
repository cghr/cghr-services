package org.cghr.survey.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


/**
 * Created by ravitej on 14/11/14.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class GPSServiceSpec extends Specification {

    @Autowired
    GPSService gpsService
    MockMvc mockMvc

    ServerSocket server

    def setupSpec() {

    }

    def setup() {

        mockMvc = MockMvcBuilders.standaloneSetup(gpsService).build()

    }

    def "should respond with status Server error when gps socket server is not running"() {

        expect:
        mockMvc.perform(get('/gps'))
                .andExpect(status().is5xxServerError())


    }


}