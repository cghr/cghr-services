package org.cghr.commons.web.controller

import groovy.transform.CompileStatic
import org.cghr.commons.db.CleanUp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by ravitej on 9/5/14.
 */

@CompileStatic
@RestController
@RequestMapping("/data/cleanup")
class CleanupService {

    @Autowired
    CleanUp cleanUp

    @RequestMapping("")
    String cleanupEntities() {

        cleanUp.cleanupTables()

    }

}
