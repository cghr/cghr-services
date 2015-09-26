package org.cghr.commons.web.controller

import org.cghr.commons.db.DbStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by ravitej on 12/2/15.
 */
@RestController
@RequestMapping("/data/reset")
class ResetChangelogs {

    @Autowired
    DbStore dbStore

    @RequestMapping("")
    Map resetAllChangelogs() {

        dbStore.execute("update datachangelog set status=null", [])
        [status: true]
    }


}
