package org.cghr.commons.db

import com.google.gson.Gson
import groovy.sql.Sql
import groovy.transform.CompileStatic

import java.sql.ResultSetMetaData

@CompileStatic
class DbAccess {

    Sql gSql;
    Gson gson = new Gson()


    DbAccess(Sql gSql) {
        this.gSql = gSql
    }

    boolean hasRows(String sql, List params = []) {

        gSql.rows(sql, params).size() > 0
    }

    Map<String, String> getRowAsMap(String sql, List params = []) {
        List rows = gSql.rows(sql, params)
        rows.size() > 0 ? rows[0] : [:] //empty map
    }

    List<Map> getRowsAsListOfMaps(String sql, List params = []) {
        gSql.rows(sql, params)
    }

    String getRowAsJson(String sql, List params = []) {
        gson.toJson(getRowAsMap(sql, params))
    }

    //overloaded
    String getRowAsJson(String dataStore, String keyField, String keyFieldValue) {
        def sql = "select * from $dataStore where $keyField=?"
        getRowAsJson(sql, [keyFieldValue])
    }

    String getRowsAsJsonArray(String sql, List params) {

        gson.toJson(gSql.rows(sql, params))
    }

    //overloaded
    String getRowsAsJsonArray(String dataStore, String keyField, String keyFieldValue) {

        String sql = "select * from $dataStore where $keyField=?"
        getRowsAsJsonArray(sql, [keyFieldValue])
    }

    String getRowsAsJsonArrayOnlyValues(String sql, List params) {

        List rows = gSql.rows(sql, params)
        gson.toJson(rows.collect { Map row -> row.values() })
    }

    String getColumnLabels(String sql, List params) {
        List columnLabels = []
        def metaClosure = { ResultSetMetaData meta ->
            (1..meta.columnCount).each {
                Integer i ->
                    columnLabels.add(meta.getColumnLabel(i))
            }
        }

        String sqlWithOneRow = sql + " LIMIT 1"
        gSql.rows(sqlWithOneRow, params, metaClosure)
        columnLabels.join(",")
    }

    void removeData(String table, String keyField, Object value) {
        def sql = "delete from $table where $keyField=?".toString()
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