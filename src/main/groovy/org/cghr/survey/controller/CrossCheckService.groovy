package org.cghr.survey.controller
import com.google.gson.Gson
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
    public String getCrossCheck(@RequestBody Map crossCheck) {

        def sql = "select $crossCheck.field from $crossCheck.entity where $crossCheck.ref=?"
        def row = dbAccess.firstRow(sql, [crossCheck.refId])
        def field = crossCheck.field
        def dbValue = row.get(field)
        dbValue = dbValue.isInteger() ? dbValue.toInteger() : dbValue

        new Gson().toJson([value: dbValue])

    }
}