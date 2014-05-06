package org.cghr.security.controller

import groovy.transform.CompileStatic
import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@CompileStatic
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
        RequestParser parser = new RequestParser()

        deleteAuthToken(parser.getAuthTokenFromCookies(request))
        //nullifyAllCookies(response)
        eraseCookies(request,response)
        return null
    }

    void deleteAuthToken(String authtoken) {

        dbAccess.removeData("authtoken", "token", authtoken)
    }

    void nullifyAllCookies(HttpServletResponse response) {

        response.addCookie(new Cookie("authtoken", null))
        response.addCookie(new Cookie("user", null))
    }
    void eraseCookies(HttpServletRequest req, HttpServletResponse resp) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null)
            for (int i = 0; i < cookies.length; i++) {
                cookies[i].setValue("");
                cookies[i].setPath("/");
                cookies[i].setMaxAge(0);
                resp.addCookie(cookies[i]);
            }
    }
}
