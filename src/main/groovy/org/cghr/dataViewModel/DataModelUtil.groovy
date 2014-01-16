package org.cghr.dataViewModel

import org.cghr.commons.db.DbAccess

import com.google.gson.Gson
import com.google.gson.JsonObject

class DataModelUtil {


	GenericDataModelTransformer dataModelTransformer
	DbAccess dbAccess

	DataModelUtil(GenericDataModelTransformer dataModelTransformer,DbAccess dbAccess) {
		this.dataModelTransformer=dataModelTransformer
		this.dbAccess=dbAccess
	}

	String constructJsonResponse(String sql,List params,String filters,String sortings) {

		Gson gson=new Gson()
		JsonObject data= gson.fromJson(dataModelTransformer.getModel(sql, params), JsonObject)
		def headings=dbAccess.getColumnLabels(sql, params)

		Map model= [headings:headings,filters:filters,sortings:sortings,data:data]
		gson.toJson(model)
	}
}
