package org.cghr.commons.web.controller

import com.google.gson.Gson
import org.cghr.commons.db.DbStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/data/dataStoreBatchService")
class DataStoreBatch {

    @Autowired
    DbStore dbStore


    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    String saveData(@RequestBody String data) {

        List<Map<String, String>> changelogs = new Gson().fromJson(data, List.class)
        dbStore.saveOrUpdateBatch(changelogs)
    }


    DataStoreBatch() {

    }

    DataStoreBatch(DbStore dbStore) {
        this.dbStore = dbStore

    }
}
