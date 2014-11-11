package org.cghr.commons.db

import groovy.sql.Sql
import groovy.transform.TupleConstructor

@TupleConstructor
class DbStore {

    Map dataStoreFactory
    Sql gSql


    void saveOrUpdate(Map data, String datastore) {

        String keyField = dataStoreFactory[datastore]
        String keyFieldValue = data[keyField]
        String keysAndValues = getKeysAndValues(data)

        List valueList = data.values() as List

        def sql = isNewData(datastore, keyField, keyFieldValue) ?
                "insert into $datastore set $keysAndValues"
                :
                "update $datastore set $keysAndValues where $keyField=$keyFieldValue"
        gSql.execute(sql, valueList)
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
