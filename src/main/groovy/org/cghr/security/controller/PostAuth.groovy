package org.cghr.security.controller

import org.cghr.security.service.UserService

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

/**
 * Created by ravitej on 3/6/14.
 */
class PostAuth {

    UserService userService

    PostAuth(UserService userService) {
        this.userService = userService
    }
    def final cookiePath = '/'

    void addCookie(String cookieName, String cookieValue, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, cookieValue)
        cookie.with {
            setMaxAge(60 * 60 * 24); setPath(cookiePath);
            response.addCookie(delegate)
        }
    }


}
