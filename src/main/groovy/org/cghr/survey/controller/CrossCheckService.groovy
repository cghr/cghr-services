package org.cghr.survey.controller
import com.google.gson.Gson
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
@Controller
@RequestMapping("/CrossCheckService")
public class CrossCheckService {

    @Autowired
    DbAccess dbAccess

    CrossCheckService() {

    }

    CrossCheckService(DbAccess dbAccess) {
        this.dbAccess = dbAccess
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    public String getCrossCheck(@RequestBody Map<String, String> crossCheck) {


        def sql = "select $crossCheck.field from $crossCheck.entity where $crossCheck.ref=?".toString()
        def value = dbAccess.getRowAsMap(sql, [crossCheck.refId])
        def fieldValue = crossCheck.field
        def dbValue = value.get(fieldValue)
        println 'field value '+fieldValue
        println 'db value '+dbValue
        dbValue = dbValue.isInteger() ? dbValue.toInteger() : dbValue
        return new Gson().toJson([value:dbValue])

    }
}