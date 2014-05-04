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

    AuthInterceptor() {

    }

    AuthInterceptor(UserService userService, RequestParser requestParser) {

        this.userService = userService
        this.requestParser = requestParser
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

        def token = requestParser.getAuthTokenFromCookies(request)
        boolean isValidToken = (token == null) ? false : (userService.isValidToken(token))

        if (!isValidToken) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value)
            return false
        } else {
            response.setStatus(HttpStatus.OK.value)
            return true
        }


    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        //System.out.println("Post-handle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        //System.out.println("After completion handle");
    }


}
