package org.cghr.security.controller

import javax.servlet.http.HttpServletRequest

class RequestParser {

    String getAuthTokenFromCookies(HttpServletRequest request) {

        request.getCookies()
                .find { "authtoken" == it.name }?.value
    }

    String getUsernameFromCookies(HttpServletRequest request) {
        request.getCookies()
                .find { "username" == it.name }?.value
    }
}
