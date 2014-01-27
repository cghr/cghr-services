package org.cghr.security.controller

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest


class RequestParser {

    String getAuthTokenFromCookies(HttpServletRequest request) {


        for (Cookie cookie in request.getCookies()) {
            if ("authtoken" == cookie.getName())
                return cookie.getValue()
        }
        return null
    }
}
