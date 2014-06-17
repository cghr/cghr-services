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
@RequestMapping("/CrossCheckService")
public class CrossCheckService {

    @Autowired
    DbAccess dbAccess

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    String getCrossCheck(@RequestBody Map crossCheckMetadata) {

        def dbValue = getCrossCheckValue(crossCheckMetadata)
        def crossCheckValue = dbValue.isInteger() ? dbValue.toInteger() : dbValue

        [value: crossCheckValue].toJson()

    }

    String getCrossCheckValue(Map crossCheck) {

        def sql = "select $crossCheck.field crossCheck from $crossCheck.entity where $crossCheck.ref=?"
        dbAccess.firstRow(sql, [crossCheck.refId]).crossCheck

    }

}