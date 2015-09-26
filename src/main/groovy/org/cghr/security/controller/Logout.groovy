package org.cghr.security.controller

import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/security/logout")
class Logout {


    @Autowired
    DbAccess dbAccess

    @Qualifier("tokenCache")
    @Autowired
    HashMap tokenCache


    @RequestMapping("")
    String invalidateSession(
            @CookieValue("authtoken") String authtoken,
            @CookieValue("username") String username, HttpServletRequest request, HttpServletResponse response) {

        deleteAuthToken(authtoken, username)
        eraseCookies(request.getCookies(), response)
    }


    void deleteAuthToken(String authtoken, String username) {

        tokenCache.remove(username)
        dbAccess.removeData("authtoken", "token", authtoken)
    }

    void eraseCookies(Cookie[] cookies, HttpServletResponse response) {

        cookies.each { Cookie cookie ->
            cookie.with {
                setValue(""); setPath("/"); setMaxAge(0)
                response.addCookie(delegate)
            }

        }
    }
}
