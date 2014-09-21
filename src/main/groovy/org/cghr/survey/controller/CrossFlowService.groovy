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

    Object crossFlowValue(Map metaData) {

        String field = metaData.field
        if (field.contains("age")) {
            return getAgeValue(metaData)
        }
        String sql = "select $metaData.field crossCheck from $metaData.entity where $metaData.ref=?".toString()
        println sql
        String dbValue = dbAccess.firstRow(sql, [metaData.refId]).crossCheck
        println dbValue
        getIntOrStringOf(dbValue)
    }

    double getAgeValue(Map metaData) {

        String field = (metaData.field).split('_')[0]
        String sql = "select $metaData.field age,$field" + "_unit age_unit from $metaData.entity where $metaData.ref=?"
        Map result = dbAccess.firstRow(sql, [metaData.refId])
        println 'result' + result
        convertToYears((result.age).toInteger(), result.age_unit)
    }


    double convertToYears(Integer age, String age_unit) {

        (age_unit == 'Days') ? (age / 365) : ((age_unit == 'Months') ? (age * 30) / 365 : age)

    }


    Object getIntOrStringOf(String value) {
        println 'get int ot string of ' + value
        if (!value)
            return null
        value.isInteger() ? value.toInteger() : value
    }

}