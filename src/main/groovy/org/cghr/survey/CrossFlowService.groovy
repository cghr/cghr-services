package org.cghr.survey

import com.google.gson.Gson
import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

import javax.servlet.http.HttpServletResponse

/**
 * Created by ravitej on 7/4/14.
 */
@Controller
@RequestMapping("/CrossFlowService")
public class CrossFlowService {

    @Autowired
    DbAccess dbAccess

    CrossFlowService() {

    }

    CrossFlowService(DbAccess dbAccess) {
        this.dbAccess = dbAccess
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    public String getCrossFlowCheck(@RequestBody String crossFlowsJson, HttpServletResponse response) {

        List<Map> crossFlows=new Gson().fromJson(crossFlowsJson,List.class)
        boolean isConditionFailing=false;

        crossFlows.each {

            def sql = "select $it.field from $it.entity where $it.ref=?"
            def value = dbAccess.getRowAsMap(sql, [it.refId])
            def fieldValue=it.field
            def dbValue=value.get(fieldValue)
            dbValue=dbValue.isInteger()?dbValue.toInteger():dbValue

            if (!Eval.me(it.field,dbValue , it.condition)){
                isConditionFailing=true
                return;

            }
        }
        isConditionFailing?'{"check":false}':'{"check":true}'
    }
}