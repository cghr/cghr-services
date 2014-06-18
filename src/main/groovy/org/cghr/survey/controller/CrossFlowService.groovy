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
    public Map getCrossFlowCheck(@RequestBody Map[] crossFlowMetadata, HttpServletResponse response) {

        isAllCrossFlowConditionsPassing(crossFlowMetadata as List) ? [check: true] : [check: false]
    }

    boolean isAllCrossFlowConditionsPassing(List crossFlows) {
        boolean isConditionPassing = true

        crossFlows.each {
            if (isConditionFailing(it, crossFlowValue(it))) {
                isConditionPassing = false
                return
            }
        }
        isConditionPassing
    }

    boolean isConditionFailing(Map crossCheckMetadata, Object crossCheckValue) {

        !Eval.me(crossCheckMetadata.field, crossCheckValue, crossCheckMetadata.condition)
    }

    String crossFlowValue(Map metaData) {

        String sql = "select $metaData.field crossCheck from $metaData.entity where $metaData.ref=?"
        String dbValue = dbAccess.firstRow(sql, [metaData.refId]).crossCheck
        dbValue.isInteger() ? dbValue.toInteger() : dbValue
    }

}