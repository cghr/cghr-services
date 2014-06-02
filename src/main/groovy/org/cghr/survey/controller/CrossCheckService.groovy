package org.cghr.survey.controller

import com.google.gson.Gson
import groovy.transform.CompileStatic
import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Created by ravitej on 7/4/14.
 */
@CompileStatic
@RestController
@RequestMapping("/CrossCheckService")
public class CrossCheckService {

    @Autowired
    DbAccess dbAccess

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public String getCrossCheck(@RequestBody Map crossCheck) {

        def sql = "select $crossCheck.field from $crossCheck.entity where $crossCheck.ref=?".toString()
        def value = dbAccess.getRowAsMap(sql, [crossCheck.refId])
        def fieldValue = crossCheck.field
        def dbValue = value.get(fieldValue)
        dbValue = dbValue.isInteger() ? dbValue.toInteger() : dbValue
        return new Gson().toJson([value: dbValue])

    }
}