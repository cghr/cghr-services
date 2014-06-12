package org.cghr.commons.db

import groovy.sql.Sql

import java.sql.ResultSetMetaData

class DbAccess {

    final Sql gSql

    DbAccess(Sql gSql) {
        this.gSql = gSql
    }

    boolean hasRows(String sql, List params = []) {

        gSql.firstRow(sql, params) ? true : false
    }

    Map<String, String> getRowAsMap(String sql, List params = []) {
        List rows = gSql.rows(sql, params)
        rows.size() > 0 ? rows[0] : [:] //empty map
    }

    Map firstRow(String sql, List params = []) {

        Map row = gSql.firstRow(sql, params)
        row ? row : [:]
    }


    List<Map> getRowsAsListOfMaps(String sql, List params = []) {
        gSql.rows(sql, params)
    }

    List<Map> rows(String sql, List params = []) {
        gSql.rows(sql, params)
    }

    String getRowAsJson(String sql, List params = []) {
        firstRow(sql, params).toJson()
    }

    String jsonRow(String sql, List params = []) {
        firstRow(sql, params).toJson()
    }

    //overloaded
    String getRowAsJson(String dataStore, String keyField, String keyFieldValue) {

        String sql = "select * from $dataStore where $keyField=?"
        jsonRow(sql, [keyFieldValue])
    }

    String jsonRow(String dataStore, String keyField, String keyFieldValue) {
        String sql = "select * from $dataStore where $keyField=?"
        jsonRow(sql, [keyFieldValue])
    }

    String getRowsAsJsonArray(String sql, List params) {
        gSql.rows(sql, params).toJson()
    }

    String rowsJsonArray(String sql, List params) {

        gSql.rows(sql, params).toJson()
    }

    //overloaded
    String getRowsAsJsonArray(String dataStore, String keyField, String keyFieldValue) {

        String sql = "select * from $dataStore where $keyField=?"
        getRowsAsJsonArray(sql, [keyFieldValue])
    }

    String rowsJsonArray(String dataStore, String keyField, String keyFieldValue) {

        String sql = "select * from $dataStore where $keyField=?"
        getRowsAsJsonArray(sql, [keyFieldValue])
    }

    String getRowsAsJsonArrayOnlyValues(String sql, List params) {

        List list = gSql.rows(sql, params).collect {
            Map row -> row.values()
        }
        list.toJson()

    }

    String getColumnLabels(String sql, List params) {

        List columnLabels = []
        gSql.rows(sql, params) { ResultSetMetaData metaData ->
            (1..metaData.columnCount).each {
                Integer i ->
                    columnLabels.add(metaData.getColumnLabel(i))
            }
        }
        columnLabels.join(",")
    }

    String columns(String sql, List params) {

        List columnLabels = []
        gSql.rows(sql, params) { ResultSetMetaData metaData ->
            (1..metaData.columnCount).each {
                Integer i ->
                    columnLabels.add(metaData.getColumnLabel(i))
            }
        }
        columnLabels.join(",")
    }


    void removeData(String table, String keyField, Object value) {
        def sql = "delete from $table where $keyField=?"
        gSql.executeUpdate(sql, [value])

    }

    //Overloaded
    void removeData(List tables) {

        tables.each {
            gSql.execute("truncate table $it".toString())
        }

    }

    List eachRow(String sql, List params, List result, Closure closure) {

        gSql.eachRow(sql, params, closure)
        return result

    }



}