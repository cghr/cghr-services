package org.cghr.commons.db

import groovy.sql.Sql

import com.google.gson.Gson

class DbAccess {


	Sql gSql;
	Gson gson = new Gson()

	DbAccess(Sql gSql) {
		this.gSql = gSql
	}

	boolean hasRows(String sql, List params) {

		def row = gSql.firstRow("select count(*) count from (${sql}) a", params)
		row.count > 0
	}

	Map<String, String> getRowAsMap(String sql, List params) {

		hasRows(sql, params) ? gSql.firstRow(sql, params) : [:] //empty map

	}

	List getRowsAsListOfMaps(String sql, List params) {

		hasRows(sql, params) ? gSql.rows(sql, params) : [] //empty list
	}

	String getRowAsJson(String sql, List params) {

		def rows = gSql.rows(sql, params)
		rows.isEmpty() ? '{}' : gson.toJson(rows[0])
	}
	//overloaded
	String getRowAsJson(String dataStore, String keyField, String keyFieldValue) {
		def sql = "select * from $dataStore where $keyField=$keyFieldValue"
		getRowAsJson(sql, [])
	}

	String getRowsAsJsonArray(String sql, List params) {

		def rows = gSql.rows(sql, params)
		rows.isEmpty() ? '[]' : gson.toJson(rows)
	}

	String getRowsAsJsonArrayOnlyValues(String sql, List params) {

		def rows = gSql.rows(sql, params)
		rows.isEmpty() ? '[]' : gson.toJson(rows.collect() { row -> row.values() })
	}

	String getColumnLabels(String sql, List params) {
		List columnLabels = []
		def metaClosure = { meta ->
			(1..meta.columnCount).each {
				columnLabels.add(meta.getColumnLabel(it))
			}
		}

		gSql.rows(sql, params, metaClosure)
		columnLabels.join(",")
	}

	void removeData(String table, String keyField, Object value) {
		def sql = "delete from $table where $keyField=?".toString()
		gSql.executeUpdate(sql, [value])

	}

    List eachRow(String sql,List params,List result,Closure closure){


        gSql.eachRow(sql,params,closure)
        return result

    }

}