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
class CrossCheckService {

    @Autowired
    DbAccess dbAccess

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    Map getCrossCheck(@RequestBody Map crossCheckMetadata) {

        String dbValue = getCrossCheckValue(crossCheckMetadata)
        [value: getIntOrStringOf(dbValue)]
    }

    String getCrossCheckValue(Map metadata) {
        
        def sql = "select $metadata.field crossCheck from $metadata.entity where $metadata.ref=?"
        dbAccess.firstRow(sql, [metadata.refId]).crossCheck
    }

    Object getIntOrStringOf(String crossCheckValue) {
        crossCheckValue.isInteger() ? crossCheckValue.toInteger() : crossCheckValue

    }

}