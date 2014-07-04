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

        List rows = dbAccess.rows(sql, params)
                .collectWithIndex { row, index ->
            [id: (index + 1), data: row.values()]
        }
        [rows: rows]
    }
}
