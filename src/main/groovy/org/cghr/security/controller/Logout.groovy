package org.cghr.security.controller

import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/security/logout")
class Logout {


    @Autowired
    DbAccess dbAccess

    Logout() {
    }

    Logout(DbAccess dbAccess) {
        this.dbAccess = dbAccess
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    String invalidateSession(HttpServletRequest request, HttpServletResponse response) {

        deleteAuthToken(getAuthToken(request))
        eraseCookies(request.getCookies(), response)
        return null
    }

    String getAuthToken(HttpServletRequest request) {

        new RequestParser().getAuthTokenFromCookies(request)
    }

    void deleteAuthToken(String authtoken) {

        dbAccess.removeData("authtoken", "token", authtoken)
    }

    void eraseCookies(Cookie[] cookies, HttpServletResponse response) {

        cookies.each {
            Cookie cookie ->
                cookie.with {
                    setValue(""); setPath("/"); setMaxAge(0)
                    response.addCookie(delegate)
                }

        }
    }
}
