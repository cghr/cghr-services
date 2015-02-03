package org.cghr.commons.db

import groovy.sql.Sql
import groovy.transform.TupleConstructor

@TupleConstructor
class DbStore {

    Map dataStoreFactory
    Sql gSql


    void saveOrUpdate(Map entity, String entityName) {

        String keyField = dataStoreFactory[entityName]
        String keyFieldValue = entity[keyField]
        String keysAndValues = getKeysAndValues(entity)

        List valueList = entity.values() as List

        def sql = isNewData(entityName, keyField, keyFieldValue) ?
                "insert into $entityName set $keysAndValues" : "update $entityName set $keysAndValues where $keyField=$keyFieldValue"

        gSql.execute(sql, valueList)
    }

    void freshSave(Map entity, String entityName) {

        String keyField = dataStoreFactory[entityName]
        String keyFieldValue = entity[keyField]
        String sql = "delete from $entityName where $keyField=?"

        if (!isNewData(entityName, keyField, keyFieldValue))
            gSql.execute(sql, keyFieldValue)

        String keysAndValues=getKeysAndValues(entity)
        List valueList=entity.values().toList()
        def insertSql="insert into $entityName set $keysAndValues"
        gSql.executeUpdate(insertSql,valueList)
    }

    String getKeysAndValues(Map data) {
        data.collect { key, value -> "$key=?" }.join(",")
    }


    void execute(String sql, List params) {
        gSql.execute(sql, params)
    }

    void saveOrUpdateFromMapList(List<Map> list, String dataStore) {
        list.each { saveOrUpdate(it, dataStore) }
    }

    void saveOrUpdateBatch(List datachangelogs) {

        datachangelogs.each { saveOrUpdate(it.data, it.datastore) }
    }

    boolean isNewData(String dataStore, String keyField, String keyFieldValue) {

        gSql.firstRow("select * from $dataStore where $keyField=?", [keyFieldValue]) ? false : true

    }

    void createDataChangeLogs(Map data, String dataStore) {

        Map log = [datastore: dataStore, data: data]
        gSql.execute("insert into datachangelog(log) values(?)", log.toJson())
    }

    void saveDataChangelogs(List changelogs) {

        gSql.withBatch("insert into datachangelog(log) values(?)") { ps ->
            changelogs.each { ps.addBatch([it]) }
        }
    }

    void eachRow(String sql, List params, Closure closure) {

        gSql.eachRow(sql, params, closure)
    }


}
