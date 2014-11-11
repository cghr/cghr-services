package org.cghr.dataViewModel

import groovy.transform.Memoized
import groovy.transform.TupleConstructor
import org.cghr.commons.db.DbAccess

@TupleConstructor
class DhtmlxGridModelTransformer implements GenericDataModelTransformer {

    DbAccess dbAccess

    /*
     * json format expected by dhtmlx client library { "rows":[
     * {"id":1,"data":["1","Foo","Linux"]}, {"id":2,"data":["2","Bar","OS X"]} ]
     * }
     */

    Map getModel(String sql, List params) {

        transformModel(dbAccess.rows(sql, params))
    }

    @Memoized
    Map transformModel(List rows) {

        List transformedList = rows.collectWithIndex { row, index ->
            [id: (index + 1), data: row.values()]
        }
        [rows: transformedList]
    }
}
