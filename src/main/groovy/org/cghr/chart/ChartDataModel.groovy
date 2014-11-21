package org.cghr.chart

import groovy.transform.CompileStatic

/**
 * Created by ravitej on 8/5/14.
 */
@CompileStatic
interface ChartDataModel {

    String getChartDataModel(String sql,List params);
}
