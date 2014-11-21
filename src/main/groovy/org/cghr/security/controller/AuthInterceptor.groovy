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

    @Override
    boolean preHandle(HttpServletRequest request,
                      HttpServletResponse response, Object handler) throws Exception {

        def token = getAuthToken(request)
        isInvalidToken(token) ? unauthorised(response) : true
    }

    String getAuthToken(HttpServletRequest request) {
        requestParser.getAuthTokenFromCookies(request)
    }

    boolean isInvalidToken(String token) {

        (token == null) ? true : !(userService.isValidToken(token))
    }

    boolean unauthorised(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value)
        return false
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
