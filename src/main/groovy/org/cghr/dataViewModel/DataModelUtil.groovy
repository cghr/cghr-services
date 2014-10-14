package org.cghr.dataViewModel

import org.cghr.commons.db.DbAccess

class DataModelUtil {


    GenericDataModelTransformer dataModelTransformer
    DbAccess dbAccess

    DataModelUtil(GenericDataModelTransformer dataModelTransformer, DbAccess dbAccess) {
        this.dataModelTransformer = dataModelTransformer
        this.dbAccess = dbAccess
    }

    String constructJsonResponse(String sql, List params, String filters, String sortings) {

        Map data = dataModelTransformer.getModel(sql, params)
        def headings = dbAccess.columns(sql, params).join(',')

        [headings: headings,
                filters: filters,
                sortings: sortings,
                data: data].toJson()

    }
}
