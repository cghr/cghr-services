package org.cghr.security.controller

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

/**
 * Created by ravitej on 3/6/14.
 */
class PostAuth {


    def final cookiePath = '/'

    void addCookie(String cookieName, String cookieValue, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, cookieValue)
        cookie.with {
            setMaxAge(60 * 60 * 24);
            setPath(cookiePath);
            response.addCookie(delegate)
        }
    }


}
