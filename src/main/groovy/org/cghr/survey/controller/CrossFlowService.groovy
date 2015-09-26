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
@RequestMapping("/survey/crossFlow")
public class CrossFlowService {

    @Autowired
    DbAccess dbAccess

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    Map getCrossFlowCheck(@RequestBody Map[] crossFlowMetadata) {

        isAnyConditionFailing(crossFlowMetadata as List) ? [check: false] : [check: true]
    }

    boolean isAnyConditionFailing(List crossFlows) {

        crossFlows.find { isConditionFailing(it, crossFlowValue(it)) }
    }

    boolean isConditionFailing(Map crossCheckMetadata, Object crossCheckValue) {

        String field = crossCheckMetadata.field.contains(" ") ? crossCheckMetadata.field.split(" ")[1] : crossCheckMetadata.field
        !Eval.me(field, crossCheckValue, crossCheckMetadata.condition)
    }

    Object crossFlowValue(Map metaData) {

        String field = metaData.field

        if (field.contains("age"))
            return getAgeValue(metaData)

        String sql = ""
        String sqlField = ""

        if (field.contains(" ")) {
            sqlField = field.split(" ")[0]
            sql = "select $sqlField crossCheck from $metaData.entity where $metaData.ref=? and $metaData.whereCondition".toString()
        } else
            sql = "select $field crossCheck from $metaData.entity where $metaData.ref=?".toString()

        String dbValue = dbAccess.firstRow(sql, [metaData.refId]).crossCheck
        getIntOrStringOf(dbValue)
    }

    double getAgeValue(Map metaData) {

        String field = (metaData.field).split('_')[0]
        String sql = "select $metaData.field age,$field" + "_unit age_unit from $metaData.entity where $metaData.ref=?"
        Map result = dbAccess.firstRow(sql, [metaData.refId])
        convertToYears((result.age).toInteger(), result.age_unit)
    }

    double convertToYears(Integer age, String age_unit) {

        (age_unit == 'Days') ? (age / 365) : ((age_unit == 'Months') ? (age * 30) / 365 : age)

    }

    Object getIntOrStringOf(String value) {
        if (!value)
            return null
        value.isInteger() ? value.toInteger() : value
    }

}