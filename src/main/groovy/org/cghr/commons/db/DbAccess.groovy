package org.cghr.commons.db

import groovy.sql.Sql

import java.sql.ResultSetMetaData

/*
Wrapper Class for Groovy Sql for Database Access
 */

class DbAccess {

    Sql gSql

    DbAccess(final Sql gSql) {
        this.gSql = gSql
    }

    Map firstRow(String sql, List params = []) {
        Map row = gSql.firstRow(sql, params)
        row ?: [:]
    }

    //Overloaded
    Map firstRow(String dataStore, String keyField, String keyFieldValue) {
        String sql = "select * from $dataStore where $keyField=?"
        firstRow(sql, [keyFieldValue])
    }

    List rows(String sql, List params = []) {
        gSql.rows(sql, params)
    }

    //Overloaded
    List rows(String dataStore, String keyField, String keyFieldValue) {
        String sql = "select * from $dataStore where $keyField=?"
        rows(sql, [keyFieldValue])
    }

    List columns(String sql, List params) {

        List columnLabels = []
        gSql.rows(sql, params) { ResultSetMetaData metaData ->
            columnLabels = (1..metaData.columnCount).collect {
                metaData.getColumnLabel(it)
            }
        }
        return columnLabels
    }


    void removeData(String table, String keyField, Object value) {

        gSql.executeUpdate("delete from $table where $keyField=?", [value])

    }

    //Overloaded
    void removeData(List tables) {
        tables.each {
            gSql.executeUpdate("truncate table $it", [])
        }
    }


}