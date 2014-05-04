package org.cghr.dataSync.controller

import org.cghr.commons.db.DbAccess
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Created by ravitej on 4/5/14.
 */
@RestController
@RequestMapping("/sync/status")
class SyncStatus {

    DbAccess dbAccess

    @RequestMapping(value = "/download", method = RequestMethod.GET, produces = "text/plain")
    String downloadTotal() {

        dbAccess.getRowAsMap("select count(*) count from inbox where impStatus is null", []).count

    }


    @RequestMapping(value = "/upload", method = RequestMethod.GET, produces = "text/plain")
    String uploadTotal() {

        dbAccess.getRowAsMap("select count(*) count from datachangelog where status is null", []).count
    }


}
