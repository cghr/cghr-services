package org.cghr.security.controller

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

class RequestParser {

    String getAuthTokenFromCookies(HttpServletRequest request) {
        
        Cookie cookie = request.getCookies().find { "authtoken" == it.name }
        cookie?.value
    }
}
