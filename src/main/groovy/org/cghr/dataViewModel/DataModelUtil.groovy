package org.cghr.dataViewModel

import com.google.gson.Gson
import groovy.transform.CompileStatic
import org.cghr.commons.db.DbAccess

@CompileStatic
class DataModelUtil {


    GenericDataModelTransformer dataModelTransformer
    DbAccess dbAccess

    Gson gson = new Gson()

    DataModelUtil(GenericDataModelTransformer dataModelTransformer, DbAccess dbAccess) {
        this.dataModelTransformer = dataModelTransformer
        this.dbAccess = dbAccess
    }

    String constructJsonResponse(String sql, List params, String filters, String sortings) {

        //JsonObject data = gson.fromJson(dataModelTransformer.getModel(sql, params), JsonObject)
        Map data = dataModelTransformer.getModel(sql, params)
        def headings = dbAccess.getColumnLabels(sql, params)

        Map model = [headings: headings, filters: filters, sortings: sortings, data: data]
        gson.toJson(model)
    }
}
