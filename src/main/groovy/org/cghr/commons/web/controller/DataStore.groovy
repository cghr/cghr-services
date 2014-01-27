package org.cghr.commons.web.controller

import org.cghr.commons.db.DbStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/data/dataStoreService")
class DataStore {

    @Autowired
    DbStore dbStore

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    String saveData(@RequestBody Map data) {

        def dataStore = data.remove("datastore")
        dbStore.saveOrUpdate(data, dataStore)
    }
}
