package org.cghr.security.controller

import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.http.Cookie

class RequestParserSpec extends Specification {

    RequestParser parser
    @Shared
    def validRequest
    @Shared
    def invalidRequest

    def setupSpec() {
        validRequest = new MockHttpServletRequest()
        def token = "ABCDEFG-12345"
        Cookie authtokenCookie = new Cookie("authtoken", token)
        def cookies = [authtokenCookie] as Cookie[]
        validRequest.setCookies(cookies)
        invalidRequest = new MockHttpServletRequest()

    }

    def setup() {

        parser = new RequestParser()
    }

    def "should get authtoken from request cookies"() {


        expect:
        parser.getAuthTokenFromCookies(request) == authtoken

        where:
        request        || authtoken
        validRequest   || "ABCDEFG-12345"
        invalidRequest || null
    }
}