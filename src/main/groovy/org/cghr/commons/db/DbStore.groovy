package org.cghr.commons.db

import groovy.sql.Sql


class DbStore {


	Map dataStoreFactory
	Sql gSql

	DbStore(Sql gSql,Map dataStoreFactory) {
		this.gSql=gSql
		this.dataStoreFactory=dataStoreFactory
	}

	void saveOrUpdate(Map data,String dataStore) {

		String keyField=dataStoreFactory.get(dataStore)
		String keyFieldValue=data.get(keyField)

		def keysAndValues=data.collect(){ key,value -> "$key=?" }.join(",")

		def sql=isNewData(dataStore,keyField,keyFieldValue)?
				"insert into $dataStore set $keysAndValues"
				:
				"update $dataStore set $keysAndValues where $keyField=$keyFieldValue"

		gSql.execute(sql, data.values() as List)
	}

	boolean isNewData(String dataStore,String keyField,String keyFieldValue) {

		List rows=gSql.rows("select * from $dataStore where $keyField=?",[keyFieldValue])
		rows.isEmpty()
	}
}
