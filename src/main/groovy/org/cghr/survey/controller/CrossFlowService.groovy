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

        isAnyConditionFailing(crossFlowMetadata as List) ? [check: false] : [check: true]
    }

    boolean isAnyConditionFailing(List crossFlows) {

        Map failingCondition = crossFlows.find { isConditionFailing(it, crossFlowValue(it)) }
        failingCondition
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