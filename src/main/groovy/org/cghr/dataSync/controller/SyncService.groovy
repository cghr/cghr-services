package org.cghr.dataSync.controller

import groovy.transform.CompileStatic
import org.cghr.dataSync.commons.SyncRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CompileStatic
@RestController
@RequestMapping("/sync/dataSync")
class SyncService {

    @Autowired
    SyncRunner syncRunner

    @RequestMapping("/{role}")
    Map synchronize(@PathVariable("role") String role) {
        syncRunner.run(role)
        [status: true]
    }

}
