package org.cghr.security.service

import org.cghr.security.controller.CORSFilter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by ravitej on 27/5/14.
 */
class CORSFilterSpec extends Specification {

    CORSFilter corsFilter = new CORSFilter()

    def setupSpec() {

    }

    def setup() {

    }

    def "should "() {

        given:
        HttpServletRequest request = new MockHttpServletRequest()
        HttpServletResponse response = new MockHttpServletResponse()

        FilterChain chain = Mock()

        when:
        corsFilter.doFilter(request, response, chain)

        then:
        1 * chain.doFilter(request, response)


    }

}