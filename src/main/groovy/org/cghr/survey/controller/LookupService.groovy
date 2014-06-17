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
@RequestMapping("/LookupService")
public class LookupService {

    @Autowired
    DbAccess dbAccess

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String getLookupData(@RequestBody Map lookup) {

        def sql = "select $lookup.field text,$lookup.field value from $lookup.entity  where $lookup.ref=?".toString()
        dbAccess.rows(sql, [lookup.refId]).toJson()

    }

}