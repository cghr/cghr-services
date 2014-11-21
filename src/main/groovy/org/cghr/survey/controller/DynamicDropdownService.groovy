package org.cghr.survey.controller

import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Created by ravitej on 7/4/14.
 */
@RestController
@RequestMapping("/dynamicDropdownService")
class DynamicDropdownService {

    @Autowired
    DbAccess dbAccess

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    Map getLookupData(@RequestBody Map metadata) {

        String sql = constructSqlFromMetadata(metadata)
        dbAccess.rows(sql, [metadata.refValue])

    }

    String constructSqlFromMetadata(Map metadata) {
        "select $metadata.field text,$metadata.field value from $metadata.entity  where $metadata.ref=?"
    }

}