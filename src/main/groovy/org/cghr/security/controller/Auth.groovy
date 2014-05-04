package org.cghr.security.controller
import org.cghr.security.model.User
import org.cghr.security.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
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

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    String authenticate(@RequestBody User user, HttpServletResponse response, HttpServletRequest request) {


        String hostname = request.getRequestURL().toURL().getHost()


        def isValidUser = userService.isValid(user, hostname)

        def httpStatus = isValidUser ? HttpStatus.OK.value : HttpStatus.FORBIDDEN.value

        response.setStatus(httpStatus)

        if (isValidUser) {

            addAuthTokenCookie(user, response)

            Cookie userCookie = new Cookie("user", userService.getUserCookieJson(user))
            userCookie.setMaxAge(60 * 60 * 24)
            userCookie.setPath(cookiePath)
            response.addCookie(userCookie)

            Cookie usernameCoookie = new Cookie("username", user.username)
            //usernameCoookie.setMaxAge(60*60*24)
            usernameCoookie.setPath(cookiePath)
            response.addCookie(usernameCoookie)

            Cookie useridCookie = new Cookie("userid", userService.getId(user))
            //useridCookie.setMaxAge(60*60*24)
            useridCookie.setPath(cookiePath)
            response.addCookie(useridCookie)

            userService.logUserAuthStatus(user, "success")
            return userService.getUserCookieJson(user)
        } else{
            userService.logUserAuthStatus(user, "fail")
            return "{}"
        }





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
