package org.cghr.security.controller

import org.cghr.security.model.User
import org.cghr.security.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/security/auth")
class Auth {

    @Autowired
    UserService userService
    @Autowired
    PostAuth postAuth

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    String authenticate(@RequestBody User user, HttpServletResponse response, HttpServletRequest request) {

        String hostname = getHostName(request)
        def isValidUser = isValidUser(user, hostname)
        def httpStatus = isValidUser ? HttpStatus.OK.value : HttpStatus.FORBIDDEN.value
        response.setStatus(httpStatus)

        if (!isValidUser) {
            userService.logUserAuthStatus(user, "fail")
            return '{}'
        }
        String authtoken = UUID.randomUUID().toString()
        userService.saveAuthToken(authtoken, user)

        //Set Cookies
        postAuth.addCookie("authtoken", authtoken, response)
        postAuth.addCookie("user", userService.getUserCookieJson(user), response)
        postAuth.addCookie("username", user.username, response)
        postAuth.addCookie("userid", userService.getId(user), response)

        //Log Auth Status
        userService.logUserAuthStatus(user, "success")
        return userService.getUserCookieJson(user)

    }

    boolean isValidUser(User user, String hostname) {
        userService.isValid(user, hostname)
    }

    String getHostName(HttpServletRequest request) {
        request.getRequestURL().toURL().getHost()

    }

}
