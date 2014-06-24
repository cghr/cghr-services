package org.cghr.dataViewModel

import org.cghr.commons.db.DbAccess

class DhtmlxGridModelTransformer implements GenericDataModelTransformer {


    DbAccess dbAccess

    DhtmlxGridModelTransformer(DbAccess dbAccess) {
        this.dbAccess = dbAccess
    }

    /*
     * json format expected by dhtmlx client library { "rows":[
     * {"id":1,"data":["1","Foo","Linux"]}, {"id":2,"data":["2","Bar","OS X"]} ]
     * }
     */

    Map getModel(String sql, List params) {

        int index = 1
        List rows = dbAccess.rows(sql, params).collect {
            [id: index++, data: it.values()]
        }
        [rows: rows]
    }
}
