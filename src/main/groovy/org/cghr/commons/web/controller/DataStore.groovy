package org.cghr.commons.web.controller

import org.cghr.commons.db.DbStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/data/dataStoreService")
class DataStore {

    @Autowired
    DbStore dbStore

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    String saveData(@RequestBody Map reqData) {

        Map data = reqData.subMap(reqData.keySet() - ['datastore'])
        String datastore = reqData.datastore
        dbStore.saveOrUpdate(data, datastore)

        dbStore.createDataChangeLogs(data, datastore)
    }


}
