package org.cghr.commons.db
import groovy.sql.Sql
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification
/**
 * Created by ravitej on 9/5/14.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class CleanupSpec extends Specification {

    @Autowired
    CleanUp cleanUp
    @Autowired
    DbTester dbTester
    @Autowired
    Sql gSql


    def setup() {

        dbTester.cleanInsert('country,user,authtoken,userlog,inbox,outbox,datachangelog,filechangelog,memberImage,sales')

    }

    def "should  truncate all tables except the Excluded Entities"() {
        when:
        cleanUp.cleanupTables()

        then:
        String sql = "select * from $table"
        gSql.rows(sql).size() == result

        where:
        table     || result
        "user"    || 5
        "sales"   || 0
        "country" || 0

    }

}