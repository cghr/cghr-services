package org.cghr.commons.entity

import groovy.transform.TupleConstructor
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore

/**
 * Created by ravitej on 26/12/14.
 */
@TupleConstructor
class Entity {


    DbAccess dbAccess
    DbStore dbStore
    Map dataStoreFactory


    Map findById(String entityName, String entityId) {

        String entityKey = getEntityKey(entityName)
        dbAccess.firstRow(entityName, entityKey, entityId)
    }

    String getEntityKey(String entityName) {

        dataStoreFactory.get(entityName)
    }

    List findAll(String entityName) {

        dbAccess.getAllRows(entityName)
    }

    List findByCriteria(String entityName, String searchKey, String searchValue) {

        dbAccess.rows(entityName, searchKey, searchValue)
    }

    void saveOrUpdate(String entityName, Map entityData) {

        dbStore.saveOrUpdate(entityData, entityName)
    }

    void saveList(String entityName, List entityList) {

        dbStore.saveOrUpdateFromMapList(entityList, entityName)
    }

    void saveVariantEntities(List entities) {

        dbStore.saveOrUpdateBatch(entities)

    }

    void delete(String entityName, String entityId) {

        String entityKey = getEntityKey(entityName)
        dbAccess.removeData(entityName, entityKey, entityId)
    }

    void log(String entityName, Map entity) {
        dbStore.createDataChangeLogs(entity, entityName)
    }

    void saveChangeLogs(List changeLogs) {

        dbStore.saveDataChangelogs(changeLogs)
    }


}
