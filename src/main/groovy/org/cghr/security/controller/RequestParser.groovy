package org.cghr.security.controller

import groovy.transform.CompileStatic

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

@CompileStatic
class RequestParser {

    String getAuthTokenFromCookies(HttpServletRequest request) {

        for (Cookie cookie in request.getCookies()) {
            if ("authtoken" == cookie.getName())
                return cookie.getValue()
        }
        return null
    }
}
