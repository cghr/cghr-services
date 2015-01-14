package org.cghr.commons.db

import groovy.sql.Sql
import groovy.transform.Memoized
import groovy.transform.TupleConstructor

import java.sql.ResultSetMetaData


@TupleConstructor
class DbAccess {

    Sql gSql


    Map firstRow(String sql, List params = []) {
        gSql.firstRow(sql, params) ?: [:]
    }

    Map firstRow(String entity, String entityKey, String entityId) {
        String sql = "select * from $entity where $entityKey=?"
        firstRow(sql, [entityId])
    }

    List rows(String sql, List params = []) {
        gSql.rows(sql, params)
    }

    List rows(String dataStore, String keyField, String keyFieldValue) {
        String sql = "select * from $dataStore where $keyField=?"
        rows(sql, [keyFieldValue])
    }

    @Memoized
    List columns(String sql, List params) {

        List columnLabels
        gSql.rows(sql, params) { ResultSetMetaData metaData ->
            columnLabels = (1..metaData.columnCount).collect { metaData.getColumnLabel(it) }
        }
        return columnLabels

    }


    void removeData(String table, String keyField, Object value) {

        gSql.executeUpdate("delete from $table where $keyField=?", [value])

    }

    void removeData(List tables) {
        tables.each { gSql.executeUpdate("truncate table $it", []) }
    }

    List getAllRows(String table) {
        rows("select * from $table")
    }
}