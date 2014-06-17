package org.cghr.security.controller

import org.cghr.security.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by ravitej on 30/4/14.
 */
class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService
    @Autowired
    RequestParser requestParser


    AuthInterceptor(UserService userService, RequestParser requestParser) {

        this.userService = userService
        this.requestParser = requestParser
    }

    AuthInterceptor() {

    }

    @Override
    boolean preHandle(HttpServletRequest request,
                      HttpServletResponse response, Object handler) throws Exception {

        def token = getAuthToken(request)

        if (!isValidToken(token)) {
            UNAUTHORISED(response)
            return false
        }
        return true
    }

    String getAuthToken(HttpServletRequest request) {
        requestParser.getAuthTokenFromCookies(request)
    }

    boolean isValidToken(String token) {

        (token == null) ? false : (userService.isValidToken(token))
    }


    void UNAUTHORISED(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value)
    }


    @Override
    void postHandle(HttpServletRequest request,
                    HttpServletResponse response, Object handler,
                    ModelAndView modelAndView) throws Exception {
    }

    @Override
    void afterCompletion(HttpServletRequest request,
                         HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }


}
