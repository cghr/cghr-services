package org.cghr.commons.web.controller

import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Created by ravitej on 12/2/15.
 */
@RestController
@RequestMapping("/sql")
class SqlExecutor {

    @Autowired
    DbAccess dbAccess

    @Autowired
    DbStore dbStore

    @RequestMapping("/read")
    Map[] getRows(@RequestParam("query") String sql) {

        dbAccess.rows(sql, [])
    }

    @RequestMapping("/exec")
    Map execSql(@RequestParam("query") String sql) {

        dbStore.execute(sql, [])
        [msg: 'Executed Successfully']

    }


}
