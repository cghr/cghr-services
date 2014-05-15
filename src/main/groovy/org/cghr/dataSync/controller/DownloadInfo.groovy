package org.cghr.dataSync.controller
import com.google.gson.Gson
import groovy.transform.CompileStatic
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
/**
 * Created by ravitej on 4/5/14.
 */
@CompileStatic
@RestController
@RequestMapping("/sync/downloadInfo")
class DownloadInfo {

    @Autowired
    DbAccess dbAccess
    @Autowired
    DbStore dbStore


    @RequestMapping(value = "/{recipient}", method = RequestMethod.GET, produces = "application/json")
    String downloadInfo(@PathVariable("recipient") Integer recipient) {

        List list = dbAccess.getRowsAsListOfMaps("select datastore,ref,refId,distList from outbox where recipient=? and dwnStatus is null", [recipient])
        dbStore.execute("update outbox set dwnStatus=1 where recipient=?",[recipient])
        new Gson().toJson(list)

    }


}
