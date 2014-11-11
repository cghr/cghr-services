package org.cghr.commons.web.controller

import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/data/dataAccessService")
class DataAccess {

    @Autowired
    DbAccess dbAccess
    @Autowired
    HashMap dataStoreFactory

    @RequestMapping(value = "/{dataStore}/{keyField}/{keyFieldValue}", method = RequestMethod.GET, produces = "application/json")
    Map getDataAsJson(
            @PathVariable final String dataStore,
            @PathVariable final String keyField, @PathVariable final String keyFieldValue) {

        dbAccess.firstRow(dataStore, keyField, keyFieldValue)
    }

    @RequestMapping(value = "/{entity}/{entityId}", method = RequestMethod.GET, produces = "application/json")
    Map getResource(
            @PathVariable final String entity,
            @PathVariable final String entityId) {

        String keyField = dataStoreFactory.get(entity)
        dbAccess.firstRow(entity, keyField, entityId)
    }


}
