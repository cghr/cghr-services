package org.cghr.chart

import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification


/**
 * Created by ravitej on 4/2/15.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class AngularChartModelSpec extends Specification {

    @Autowired
    DbTester dt

    @Autowired
    AngularChartModel angularChartModel

    def setup() {

        dt.cleanInsert("country")
    }

    def "should transform sql rows to chart model "() {

        given:
        String sql = "select * from country where continent=?"
        List params = ["asia"]

        expect:
        angularChartModel.getChartDataModel(sql, params) == [series: ["name", "continent"], data: [
                [x: 1, y: ['india', 'asia']],
                [x: 2, y: ['pakistan', 'asia']],
                [x: 3, y: ['srilanka', 'asia']]
        ]]

    }


}