package org.cghr.security.controller

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
@RequestMapping("/security/logout")
class Logout {


	@Autowired DbAccess dbAccess
	Logout() {
	}
	Logout(DbAccess dbAccess) {
		this.dbAccess=dbAccess
	}

	@RequestMapping(value="",method=RequestMethod.GET)
	String invalidateSession(HttpServletRequest request,HttpServletResponse response) {
		RequestParser parser=new RequestParser()

		deleteAuthToken(parser.getAuthTokenFromCookies(request))
		nullifyAllCookies(response)
		return null
	}
	void deleteAuthToken(String authtoken) {

		dbAccess.removeData("authtoken","token",authtoken)
	}
	void nullifyAllCookies(HttpServletResponse response) {

		response.addCookie(new Cookie("authtoken",null))
		response.addCookie(new Cookie("user",null))
	}
}
