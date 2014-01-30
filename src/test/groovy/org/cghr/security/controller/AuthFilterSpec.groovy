package org.cghr.security.controller

import org.cghr.security.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.FilterChain

@ContextConfiguration(locations = "classpath:spring-context.xml")
class AuthFilterSpec extends Specification {

    @Shared
    AuthFilter filter


    @Shared
    def authtoken = "ABCDEFG-12345"


    def setupSpec() {
        RequestParser mockParser = Stub() {
            getAuthTokenFromCookies(_) >>> [authtoken, null]
        }

        UserService mockUserService = Stub() {
            isValidToken(authtoken) >> true
            isValidToken(null) >> false
        }
        filter = new AuthFilter(mockUserService, mockParser)
    }


    def "should authorise valid User"() {

        given:
        FilterChain chain = Mock()

        when:
        filter.doFilter(request, response, chain)

        then:
        response.status == result
        count * chain.doFilter(request, response)


        where:
        count | request                      | response                      || result
        1     | new MockHttpServletRequest() | new MockHttpServletResponse() || HttpStatus.OK.value
        0     | new MockHttpServletRequest() | new MockHttpServletResponse() || HttpStatus.UNAUTHORIZED.value


    }
}