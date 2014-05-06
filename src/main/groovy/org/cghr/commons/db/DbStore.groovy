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

    void saveOrUpdate(Map data, String dataStore) {

        String keyField = dataStoreFactory.get(dataStore)
        String keyFieldValue = data.get(keyField)

        def keysAndValues = data.collect() { key, value -> "$key=?" }.join(",")

        def sql = isNewData(dataStore, keyField, keyFieldValue) ?
                "insert into $dataStore set $keysAndValues"
                :
                "update $dataStore set $keysAndValues where $keyField=$keyFieldValue"

        gSql.execute(sql, data.values() as List)
    }

    void saveOrUpdateFromMapList(List<Map> list, String dataStore) {

        for (Map data : list)
            saveOrUpdate(data, dataStore)
    }

    void saveOrUpdateBatch(List<Map> datachangelogs) {

        datachangelogs.each {
            log ->
                saveOrUpdate(log.get('data'), log.get('datastore'))
        }


    }

    boolean isNewData(String dataStore, String keyField, String keyFieldValue) {

        List rows = gSql.rows("select * from $dataStore where $keyField=?", [keyFieldValue])
        rows.isEmpty()
    }

    void createDataChangeLogs(Map data, String dataStore) {

        def sql = "insert into datachangelog(log) values(?)".toString()
        Map log = [dataStore: dataStore, data: data];
        gSql.execute(sql, new Gson().toJson(log));

    }
}
