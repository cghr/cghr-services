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
@RequestMapping("/data/dataStoreService")
class DataStore {

    @Autowired
    DbStore dbStore

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    String saveData(@RequestBody Map data) {

        String dataStore = data.remove("datastore")
        dbStore.saveOrUpdate(data, dataStore)

        //Create Changelogs
        dbStore.createDataChangeLogs(data,dataStore)
    }

    DataStore() {

    }

    DataStore(DbStore dbStore) {

        this.dbStore = dbStore
    }
}
