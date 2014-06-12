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

    Map firstRow(String sql, List params = []) {
        Map row = gSql.firstRow(sql, params)
        row ? row : [:]
    }

    Map firstRow(String dataStore, String keyField, String keyFieldValue) {
        String sql = "select * from $dataStore where $keyField=?"
        firstRow(sql, [keyFieldValue])
    }

    List<Map> rows(String sql, List params = []) {
        gSql.rows(sql, params)
    }

    List rows(String dataStore, String keyField, String keyFieldValue) {
        String sql = "select * from $dataStore where $keyField=?"
        rows(sql, [keyFieldValue])
    }

    String getRowsAsJsonArrayOnlyValues(String sql, List params) {

        gSql.rows(sql, params).collect {
            Map row -> row.values()
        }.toJson()

    }

    List columns(String sql, List params) {

        List columnLabels = []
        gSql.rows(sql, params) { ResultSetMetaData metaData ->
            (1..metaData.columnCount).each {
                Integer i ->
                    columnLabels.add(metaData.getColumnLabel(i))
            }
        }
        columnLabels
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