package org.cghr.commons.web.controller

import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Created by ravitej on 25/11/14.
 */
@RestController
@RequestMapping("/entity")
class EntityService {

    @Autowired
    DbAccess dbAccess
    @Autowired
    DbStore dbStore
    @Autowired
    HashMap dataStoreFactory



    @RequestMapping("/{entityName}/{entityId}")
    Map getEntity(
            @PathVariable("entityName") String entityName,
            @PathVariable("entityId") String entityId) {

        findEntityById(entityName, entityId)

    }



    @RequestMapping("/{entityName}")
    Map[] getEntityList(@PathVariable("entityName") String entityName) {

        findEntityList(entityName)

    }



    @RequestMapping(value = "/{entityName}", method = RequestMethod.POST, consumes = "application/json")
    String saveOrUpdateEntity(@RequestBody Map entity, @PathVariable("entityName") String entityName) {

        dbStore.saveOrUpdate(entity, entityName)
        dbStore.createDataChangeLogs(entity, entityName)
        return "saved successfully"
    }



    @RequestMapping(value = "/{entityName}/{entityId}", method = RequestMethod.DELETE)
    String deleteEntity(@PathVariable("entityName") String entityName,
                        @PathVariable("entityId") String entityId) {

        String entityKey = getEntityKey(entityName)
        dbAccess.removeData(entityName, entityKey, entityId)
    }



    Map[] findEntityList(String entityName) {
        dbAccess.getAllRows(entityName)
    }



    Map findEntityById(String entityName, String entityId) {

        String entityKey = getEntityKey(entityName)
        entityKey ? dbAccess.firstRow(entityName, entityKey, entityId) : [:]
    }



    String getEntityKey(String entityName) {

        dataStoreFactory?."$entityName"
    }

}
