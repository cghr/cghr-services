package org.cghr.survey.controller

import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse

/**
 * Created by ravitej on 7/4/14.
 */

@RestController
@RequestMapping("/CrossFlowService")
public class CrossFlowService {

    @Autowired
    DbAccess dbAccess

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public Map getCrossFlowCheck(@RequestBody Map[] crossFlowsJson, HttpServletResponse response) {

        List crossFlows = crossFlowsJson as List
        boolean isConditionFailing = false;

        crossFlows.each {
            def sql = "select $it.field from $it.entity where $it.ref=?"
            def row = dbAccess.firstRow(sql, [it.refId])
            def field = it.field
            def dbValue = row.get(field)
            dbValue = dbValue.isInteger() ? dbValue.toInteger() : dbValue

            if (!Eval.me(it.field, dbValue, it.condition)) {
                isConditionFailing = true
                return;

            }
        }
        isConditionFailing ? [check: false] : [check: true]
    }
}