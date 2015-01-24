package org.cghr.dataSync.controller
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
/**
 * Created by ravitej on 4/5/14.
 */
@RestController
@RequestMapping("/sync/downloadInfo")
class DownloadInfo {

    @Autowired
    DbAccess dbAccess
    @Autowired
    DbStore dbStore


    @RequestMapping("/{recipient}")
    Map[] downloadInfo(@PathVariable Integer recipient) {

        String sql = "select datastore,ref,refId,distList from outbox where recipient=? and dwnStatus is null"
        List list = dbAccess.rows(sql, [recipient])
        dbStore.execute("update outbox set dwnStatus=1 where recipient=?", [recipient])
        list
    }


}
