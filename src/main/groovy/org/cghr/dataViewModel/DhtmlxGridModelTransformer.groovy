package org.cghr.dataViewModel
import com.google.gson.Gson
import groovy.sql.GroovyResultSet
import groovy.sql.Sql
import groovy.transform.CompileStatic

@CompileStatic
class DhtmlxGridModelTransformer implements GenericDataModelTransformer {


    Sql gSql

    DhtmlxGridModelTransformer(Sql gSql) {
        this.gSql = gSql
    }

    /*
     * json format expected by dhtmlx client library { "rows":[
     * {"id":1,"data":["1","Foo","Linux"]}, {"id":2,"data":["2","Bar","OS X"]} ]
     * }
     */

    String getModel(String sql, List params) {

        int i = 1
        def rows = []
        def closure = { GroovyResultSet row ->

            def rowMap = [id: i++, data: row.toRowResult().values()]
            rows << rowMap
        }
        gSql.eachRow(sql, params, closure)
        Map model = [rows: rows]
        new Gson().toJson(model)
    }
}
