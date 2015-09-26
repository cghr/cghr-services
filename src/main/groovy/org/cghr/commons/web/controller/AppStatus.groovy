package org.cghr.commons.web.controller

import groovy.transform.CompileStatic
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by ravitej on 7/1/15.
 */

@CompileStatic
@RestController
@RequestMapping("/app/status")
class AppStatus {

    @RequestMapping("")
    Map appStatus() {

        [status: "App is running"]

    }

}
