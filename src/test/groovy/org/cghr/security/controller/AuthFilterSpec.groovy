package org.cghr.security.controller
import groovy.sql.Sql
import org.cghr.security.service.UserService
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.servlet.FilterChain

@ContextConfiguration(locations = "classpath:spring-context.xml")
class AuthFilterSpec extends Specification {

    AuthFilter filter
    def request = new MockHttpServletRequest()
    def response = new MockHttpServletResponse()
    FilterChain chain = Mock()


    @Autowired
    Sql gSql
    @Autowired
    DbTester dt
    def authtoken = "ABCDEFG-12345"



    def setup() {

        RequestParser mockParser = Stub() {
            getAuthTokenFromCookies(request) >>> [authtoken, null]
        }

        UserService mockUserService = Stub() {
            isValidToken(authtoken) >>> [true, false]
        }
        filter = new AuthFilter(mockUserService, mockParser)
        dt.clean("authtoken")
    }

    def "should authorise valid User"() {
        given:
        def response1 = new MockHttpServletResponse() //success case
        def response2 = new MockHttpServletResponse() //fail case

        when:
        filter.doFilter(request, response1, chain)
        filter.doFilter(request, response2, chain)

        then:
        response1.status == HttpStatus.OK.value
        response2.status == HttpStatus.UNAUTHORIZED.value
        1 * chain.doFilter(request, response1)




    }
}