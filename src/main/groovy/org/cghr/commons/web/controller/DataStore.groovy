package org.cghr.commons.web.controller
import org.cghr.commons.db.DbStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/data/dataStoreService")
class DataStore {

    @Autowired
    DbStore dbStore

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    String saveData(@RequestBody  final String reqData) {

        Map data=reqData.jsonToMap()
        String datastore = data.remove("datastore")
        dbStore.saveOrUpdate(data, datastore)

        //Create Changelogs
        dbStore.createDataChangeLogs(data,datastore)
    }


}
