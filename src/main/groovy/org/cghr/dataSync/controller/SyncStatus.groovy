package org.cghr.dataSync.controller

import groovy.transform.CompileStatic
import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse

/**
 * Created by ravitej on 4/5/14.
 */
@CompileStatic
@RestController
@RequestMapping("/sync/status")
class SyncStatus {

    @Autowired
    DbAccess dbAccess

    @RequestMapping("/download")
    String downloadTotal() {

        dbAccess.getRowAsMap("select count(*) count from inbox where impStatus is null", []).count

    }


    @RequestMapping("/upload")
    String uploadTotal() {

        dbAccess.getRowAsMap("select count(*) count from datachangelog where status is null", []).count
    }

    @RequestMapping(value = "/manager",method = RequestMethod.GET ,produces = "application/json")
    Map isManager(HttpServletResponse response) {

        String role = dbAccess.getRowAsMap("select role from authtoken order by id desc limit 1",[]).role
        role == 'manager' ? [status: true] : [status:false]
    }


}
