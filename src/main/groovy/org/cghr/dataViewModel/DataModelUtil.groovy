package org.cghr.dataViewModel

import groovy.transform.TupleConstructor
import org.cghr.commons.db.DbAccess

@TupleConstructor
class DataModelUtil {


    GenericDataModelTransformer dataModelTransformer
    DbAccess dbAccess


    String constructJsonResponse(String sql, List params, String filters, String sortings) {

        Map data = dataModelTransformer.getModel(sql, params)
        def headings = dbAccess.columns(sql, params).join(',')

        [headings: headings,
         filters : filters,
         sortings: sortings,
         data    : data].toJson()

    }
}
