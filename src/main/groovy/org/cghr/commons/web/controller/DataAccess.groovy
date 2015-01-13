package org.cghr.commons.web.controller
import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/data/dataAccessService")
class DataAccess {

    @Autowired
    DbAccess dbAccess
    @Autowired
    HashMap dataStoreFactory

    @RequestMapping("/{dataStore}/{keyField}/{keyFieldValue}")
    Map getDataAsJson(
            @PathVariable  String dataStore,
            @PathVariable  String keyField, @PathVariable  String keyFieldValue) {

        dbAccess.firstRow(dataStore, keyField, keyFieldValue)
    }

    @RequestMapping("/{entity}/{entityId}")
    Map getResource(
            @PathVariable  String entity,
            @PathVariable  String entityId) {

        String keyField = dataStoreFactory.get(entity)
        dbAccess.firstRow(entity, keyField, entityId)
    }


}
