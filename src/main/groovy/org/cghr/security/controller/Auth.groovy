package org.cghr.security.controller

import org.cghr.security.model.User
import org.cghr.security.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/security/auth")
class Auth {

    @Autowired
    UserService userService
    final def cookiePath = '/'

    Auth(UserService userService) {
        this.userService = userService
    }

    Auth() {
    }

    @RequestMapping(value="",method = RequestMethod.POST)
    String authenticate(@RequestBody User user, HttpServletResponse response,HttpServletRequest request) {



        String hostname=request.getRequestURL().toURL().getHost()


        def isValidUser = userService.isValid(user,hostname)
        def httpStatus = isValidUser ? HttpStatus.OK.value : HttpStatus.FORBIDDEN.value

        response.setStatus(httpStatus)

        if (isValidUser) {

            addAuthTokenCookie(user, response)
            response.addCookie(new Cookie("user", userService.getUserCookieJson(user)))
            response.addCookie(new Cookie("username", user.username))
            response.addCookie(new Cookie("userid", userService.getId(user)))
            userService.logUserAuthStatus(user, "success")
        } else
            userService.logUserAuthStatus(user, "fail")



        return userService.getUserJson(user)
    }

    void addAuthTokenCookie(User user, HttpServletResponse response) {

        String authtoken = UUID.randomUUID().toString();
        Cookie tokenCookie = new Cookie("authtoken", authtoken);
        tokenCookie.setMaxAge(60 * 60 * 24); // 1 day
        tokenCookie.setPath(cookiePath);

        response.addCookie(tokenCookie);
        userService.saveAuthToken(authtoken, user);

    }
}
