package org.cghr.security.controller

import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Specification

import javax.servlet.http.Cookie

class RequestParserSpec extends Specification {

    RequestParser parser

    def setupSpec() {
    }

    def setup() {

        parser = new RequestParser()
    }

    def "should get authtoken from request cookies"() {

        given:
        def request = new MockHttpServletRequest()
        def authtoken = "ABCDEFG-12345"
        Cookie authtokenCookie = new Cookie("authtoken", authtoken)
        def cookies = [authtokenCookie] as Cookie[]
        request.setCookies(cookies)

        expect:
        parser.getAuthTokenFromCookies(request) == authtoken
    }
}