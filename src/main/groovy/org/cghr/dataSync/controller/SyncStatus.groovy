package org.cghr.dataSync.controller
import groovy.transform.CompileStatic
import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
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
        getCount("select count(*) count from inbox where impStatus is null")
    }

    @RequestMapping("/upload")
    String uploadTotal() {
        getCount("select count(*) count from datachangelog where status is null")
    }

    @RequestMapping("/fileupload")
    String fileUploadTotal() {
        getCount("select count(*) count from filechangelog where status is null")
    }

    @RequestMapping(value = "/manager", method = RequestMethod.GET, produces = "application/json")
    Map isManager() {

        String role = dbAccess.firstRow("select role from authtoken order by id desc limit 1", []).role
        role == 'manager' ? [status: true] : [status: false]
    }

    String getCount(String sql) {

        dbAccess.firstRow(sql, []).count
    }


}
