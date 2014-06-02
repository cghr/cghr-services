package org.cghr.dataViewModel

import com.google.gson.Gson
import groovy.sql.Sql

class DhtmlxGridModelTransformer implements GenericDataModelTransformer {


    Sql gSql

    DhtmlxGridModelTransformer(Sql gSql) {
        this.gSql = gSql
    }
    Gson gson = new Gson()

    /*
     * json format expected by dhtmlx client library { "rows":[
     * {"id":1,"data":["1","Foo","Linux"]}, {"id":2,"data":["2","Bar","OS X"]} ]
     * }
     */

    Map getModel(String sql, List params) {
        int i = 1
        List rows = gSql.rows(sql, params).collect {
            [id: i++, data: it.values()]
        }
        [rows: rows]
    }
}
