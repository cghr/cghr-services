package org.cghr.chart

import com.google.gson.Gson
import org.cghr.GenericGroovyContextLoader
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification


/**
 * Created by ravitej on 9/5/14.
 */
@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class AngularChartDataModelSpec extends Specification {

    @Autowired
    AngularChartDataModel angularChartDataModel
    @Autowired
    DbTester dbTester

    def setup() {
        dbTester.cleanInsert('sales')

    }

    def "should "() {

        given:
        Map model = [series: ['total', 'month'], data: [
                [x: 'india', y: [100, 20]],
                [x: 'pakistan', y: [80, 10]],
                [x: 'srilanka', y: [40, 20]],
        ]]
        expect:
        angularChartDataModel.getChartDataModel("select * from sales",[])==new Gson().toJson(model)


    }

}