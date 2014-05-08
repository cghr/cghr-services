package org.cghr.commons.web.controller

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
@RequestMapping("/data/dataStoreBatchService")
class DataStoreBatch {

    @Autowired
    DbStore dbStore


    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    String saveData(@RequestBody Map[] data) {

        List<Map<String, String>> changelogs = data as List
        //new Gson().fromJson(data, List.class)
        dbStore.saveOrUpdateBatch(changelogs)

        //Create Changelogs
//        changelogs.each {
//            Map log ->
//            dbStore.createDataChangeLogs((Map)log.get('data'),(String)log.get('datastore'))
//        }
    }


    DataStoreBatch() {

    }

    DataStoreBatch(DbStore dbStore) {
        this.dbStore = dbStore

    }
}
