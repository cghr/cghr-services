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
@RequestMapping("/survey/lookup")
public class LookupService {

    @Autowired
    DbAccess dbAccess

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    List getLookupData(@RequestBody Map lookup) {

        String sql = constructSqlFromMetadata(lookup)
        dbAccess.rows(sql, [lookup.refId])

    }

    String constructSqlFromMetadata(Map lookup) {
        String sql = "select $lookup.field text,$lookup.field value from $lookup.entity  where $lookup.ref=? "
        String whereCondition = (lookup.condition) ? " and $lookup.condition" : "";

        sql + whereCondition
    }

}