package org.cghr.commons.web.controller

import com.google.gson.Gson
import groovy.transform.CompileStatic
import org.cghr.commons.db.DbStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@CompileStatic
@Controller
@RequestMapping("/data/dataStoreService")
class DataStore {

    @Autowired
    DbStore dbStore
    Gson gson=new Gson()

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    String saveData(@RequestBody String reqData) {

        Map data=gson.fromJson(reqData,Map)
        String datastore = data.remove("datastore")
        dbStore.saveOrUpdate(data, datastore)

        //Create Changelogs
        dbStore.createDataChangeLogs(data,datastore)
    }


}
