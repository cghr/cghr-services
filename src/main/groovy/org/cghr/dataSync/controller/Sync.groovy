package org.cghr.dataSync.controller

import org.cghr.dataSync.commons.SyncRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sync/dataSync")
class Sync {

    @Autowired
    SyncRunner syncRunner

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    String synchronize() {

        syncRunner.run()

    }

}
