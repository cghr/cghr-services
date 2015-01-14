package org.cghr.commons.web.controller

import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletRequest

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
    @Autowired
    String serverBaseUrl


    @RequestMapping("/{entityName}/{entityId}")
    Map getEntity(
            @PathVariable String entityName,
            @PathVariable String entityId) {

        findEntityById(entityName, entityId)

    }


    @RequestMapping("/{entityName}")
    Map[] getEntityList(@PathVariable String entityName) {

        findEntityList(entityName)

    }

    @RequestMapping("/{entityName}/{property}/{propertyValue}")
    Map[] searchEntityList(
            @PathVariable String entityName, @PathVariable String property, @PathVariable String propertyValue) {

        dbAccess.rows(entityName,property,propertyValue)
    }


    @RequestMapping(value = "/{entityName}", method = RequestMethod.POST, consumes = "application/json")
    String saveOrUpdateEntity(@RequestBody Map entity, @PathVariable String entityName) {

        dbStore.saveOrUpdate(entity, entityName)
        dbStore.createDataChangeLogs(entity, entityName)
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    String saveData(@RequestBody final Map[] data, HttpServletRequest request) {

        List changelogs = data.toList()
        dbStore.saveOrUpdateBatch(changelogs)

        String requestHost = request.serverName
        if (isNotSeverHost(requestHost))
            generateChangelogs(changelogs)

    }


    @RequestMapping(value = "/{entityName}/{entityId}", method = RequestMethod.DELETE)
    String deleteEntity(@PathVariable String entityName,
                        @PathVariable String entityId) {

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

    void generateChangelogs(List changelogs) {
        dbStore.saveDataChangelogs(changelogs)
    }

    String getServerHost() {
        serverBaseUrl.toURL().getHost()
    }

    boolean isNotSeverHost(String requestHost) {
        !(requestHost == serverHost && requestHost != 'localhost')
    }

}
