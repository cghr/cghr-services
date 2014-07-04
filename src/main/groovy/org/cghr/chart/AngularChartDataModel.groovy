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

        def rows = dbAccess.rows(sql, params)
        List columnLabels = dbAccess.columns(sql, params)

        [series: columnLabels - columnLabels.first(),
                data: transformToChartModel(rows)].toJson()
    }

    List transformToChartModel(List rows) {

        rows.collectWithMapValues { List rowValues ->

            [x: rowValues.first(),
                    y: rowValues - rowValues.first()]
        }

    }


}
