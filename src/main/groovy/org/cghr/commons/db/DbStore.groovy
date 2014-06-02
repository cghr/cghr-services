package org.cghr.commons.db

import com.google.gson.Gson
import groovy.sql.Sql

class DbStore {


    Map dataStoreFactory
    Sql gSql

    DbStore(Sql gSql, Map dataStoreFactory) {
        this.gSql = gSql
        this.dataStoreFactory = dataStoreFactory
    }

    void saveOrUpdate(Map data, String datastore) {

        String keyField = dataStoreFactory."$datastore"
        String keyFieldValue = data."$keyField"
        def keysAndValues = data.collect { key, value -> "$key=?" }.join(",")

        def sql = isNewData(datastore, keyField, keyFieldValue) ?
                "insert into $datastore set $keysAndValues"
                :
                "update $datastore set $keysAndValues where $keyField=$keyFieldValue"
        gSql.execute(sql, data.values() as List)
    }


    void execute(String sql, List params) {
        gSql.execute(sql, params)
    }

    void saveOrUpdateFromMapList(List<Map> list, String dataStore) {
        list.each { saveOrUpdate(it, dataStore) }
    }

    void saveOrUpdateBatch(List<Map> datachangelogs) {

        datachangelogs.each { saveOrUpdate(it.data, it.datastore) }
    }

    boolean isNewData(String dataStore, String keyField, String keyFieldValue) {

        gSql.rows("select * from $dataStore where $keyField=?", [keyFieldValue]).size() == 0

    }

    void createDataChangeLogs(Map data, String dataStore) {
        Map log = [datastore: dataStore, data: data];
        gSql.execute("insert into datachangelog(log) values(?)", new Gson().toJson(log));

    }
}
