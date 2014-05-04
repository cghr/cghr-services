package org.cghr.security.controller
import org.cghr.security.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthInterceptorSpec extends Specification {

    @Shared
    AuthInterceptor authInterceptor


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
        authInterceptor = new AuthInterceptor(mockUserService, mockParser)
    }


    def "should authorise valid User"() {

        given:
        HttpServletRequest request = new MockHttpServletRequest()
        HttpServletResponse response = new MockHttpServletResponse()

        expect:
        authInterceptor.preHandle(request,response,new Object())==true
        authInterceptor.preHandle(request,response,new Object())==false
        response.status==HttpStatus.UNAUTHORIZED.value


    }
}