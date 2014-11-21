package org.cghr.chart

import groovy.transform.Memoized
import groovy.transform.TupleConstructor
import org.cghr.commons.db.DbAccess

/**
 * Created by ravitej on 8/5/14.
 */
@TupleConstructor
class AngularChartDataModel implements ChartDataModel {

    DbAccess dbAccess
    /*
    Example Data Format expected by Javascript Angular Chart
    {"series":["total","month"],"data":[{"x":"india","y":[100,20]},{"x":"pakistan","y":[80,10]},{"x":"srilanka","y":[40,20]}]}
    */

    @Override
    String getChartDataModel(String sql, List params) {

        def rows = dbAccess.rows(sql, params)
        List columnLabels = dbAccess.columns(sql, params)

        [series: columnLabels - columnLabels.first(),
         data  : transformToChartModel(rows)].toJson()
    }

    @Memoized
    List transformToChartModel(List<Map> rows) {


        rows.collectWithMapValues { List mapValues ->

            [x: mapValues.first(),
             y: mapValues - mapValues.first()]
        }

    }


}
