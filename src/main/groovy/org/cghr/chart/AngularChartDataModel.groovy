package org.cghr.chart

import com.google.gson.Gson
import org.cghr.commons.db.DbAccess

/**
 * Created by ravitej on 8/5/14.
 */
class AngularChartDataModel implements ChartDataModel {

    DbAccess dbAccess
    Gson gson = new Gson()

    AngularChartDataModel(DbAccess dbAccess) {
        this.dbAccess = dbAccess
    }

    /*
    Data Format expected by Javascript Angular Chart
    {"series":["total","month"],"data":[{"x":"india","y":[100,20]},{"x":"pakistan","y":[80,10]},{"x":"srilanka","y":[40,20]}]}
    */

    @Override
    String getChartDataModel(String sql, List params) {

        Map model = [series: [], data: []]

        List<Map> rows = dbAccess.getRowsAsListOfMaps(sql, params)
        String columns = dbAccess.getColumnLabels(sql, params)

        List cols = columns.split(',') as List
        cols.remove(0) // Remove First column name
        model.series = cols

        model.data = rows.collect {
            Map row ->
                List values = row.values().toList()
                String label = values.first() //set first column value in X-axis
                values.remove(0) //Remove first column value in y-axis values
                Map rowData = [x: label, y: values]

        }
        return gson.toJson(model)
    }
}
