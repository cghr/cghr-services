package org.cghr.chart

import org.cghr.commons.db.DbAccess

/**
 * Created by ravitej on 8/5/14.
 */
class AngularChartDataModel implements ChartDataModel {

    final DbAccess dbAccess

    AngularChartDataModel(DbAccess dbAccess) {
        this.dbAccess = dbAccess
    }

    /*
    Data Format expected by Javascript Angular Chart
    {"series":["total","month"],"data":[{"x":"india","y":[100,20]},{"x":"pakistan","y":[80,10]},{"x":"srilanka","y":[40,20]}]}
    */

    @Override
    String getChartDataModel(String sql, List params) {

        Map chartModel = [series: [], data: []]

        def rows = dbAccess.rows(sql, params)
        List columns = dbAccess.columns(sql, params)

        [series: columns - columns.first(),
                data: transformToChartModel(rows)
        ].toJson()
    }

    List transformToChartModel(List rows) {

        rows.collect {
            Map row ->
                List values = row.values() as List

                [x: values.first(),
                        y: values - values.first()]
        }
    }


}
