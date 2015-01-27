package org.cghr.security.controller

import org.cghr.security.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class AuthInterceptorSpec extends Specification {


    @Autowired
    AuthInterceptor authInterceptor

    def authtoken = "ABCDEFG-12345"

    def setup() {
        RequestParser mockParser = Stub() {
            getAuthTokenFromCookies(_) >>> [authtoken,null]
            getUsernameFromCookies(_) >>> ["demo",null]
        }
        UserService mockUserService = Stub() {
            isValidToken(authtoken) >> true
            isValidToken(null) >> false

            isUserAuthorised("demo") >> true
            isUserAuthorised(null) >> false
        }
        authInterceptor.requestParser = mockParser
        authInterceptor.userService = mockUserService

    }


    def "should authorise/unAuthorise user based on valid authtoken"() {

        given:
        HttpServletRequest request = new MockHttpServletRequest()
        HttpServletResponse response = new MockHttpServletResponse()

        expect:
        authInterceptor.preHandle(request, response, new Object()) == true
        response.status == HttpStatus.OK.value

        authInterceptor.preHandle(request, response, new Object()) == false
        response.status == HttpStatus.UNAUTHORIZED.value

    }


}