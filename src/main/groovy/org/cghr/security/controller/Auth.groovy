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
        isInvalidUser(user, hostname) ? authFailure(user, response) : authSuccessful(user, response)

    }

    String generateAuthToken() {
        UUID.randomUUID().toString()
    }

    String authSuccessful(User user, HttpServletResponse response) {

        String authtoken = generateAuthToken()
        saveAuthToken(authtoken, user)
        addCookies(user, authtoken, response)

        userService.logUserAuthStatus(user, "success")
        getUserJson(user)
    }

    String authFailure(User user, HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value)
        userService.logUserAuthStatus(user, "fail")
        return '{}'
    }

    void saveAuthToken(String authtoken, User user) {
        userService.saveAuthToken(authtoken, user)
    }

    String getUserJson(User user) {
        userService.getUserCookieJson(user)
    }

    void addCookies(User user, String authtoken, HttpServletResponse response) {

        postAuth.with {
            addCookie("authtoken", authtoken, response)
            addCookie("user", userService.getUserCookieJson(user), response)
            addCookie("username", user.username, response)
            addCookie("userid", userService.getId(user), response)
        }
    }


    boolean isInvalidUser(User user, String hostname) {
        !userService.isValid(user, hostname)
    }

    String getHostName(HttpServletRequest request) {
        request.getRequestURL().toURL().getHost()

    }

}
