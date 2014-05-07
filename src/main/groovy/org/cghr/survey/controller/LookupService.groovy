package org.cghr.survey.controller

import groovy.transform.CompileStatic
import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
/**
 * Created by ravitej on 7/4/14.
 */
@CompileStatic
@Controller
@RequestMapping("/LookupService")
public class LookupService {

    @Autowired
    DbAccess dbAccess

    LookupService(){

    }
    LookupService(DbAccess dbAccess){

        this.dbAccess=dbAccess
    }

    @RequestMapping(value = "",method = RequestMethod.POST,produces = "application/json", consumes = "application/json")
    @ResponseBody
    public String getLookupData(@RequestBody Map<String, String> lookup) {

        def sql = "select $lookup.field text,$lookup.field value from $lookup.entity  where $lookup.ref=?".toString()
        return dbAccess.getRowsAsJsonArray(sql, [lookup.refId])

    }

}