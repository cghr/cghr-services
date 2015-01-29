package org.cghr.commons.web.controller

import org.cghr.startupTasks.DbImport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by ravitej on 29/1/15.
 */
@RestController
@RequestMapping("/app/sqlImport")
class SqlImport {

    @Autowired
    DbImport dbImport

    @RequestMapping("")
    String importSql() {

        dbImport.importSqlScripts()
    }


}
