package org.cghr.startupTasks
import groovy.sql.Sql
import org.cghr.commons.db.DbAccess
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification
/**
 * Created by ravitej on 15/2/15.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class ChangeLogCleanupSpec extends Specification {

    @Autowired
    DbAccess dbAccess

    ChangeLogCleanup changeLogCleanup

    @Autowired
    DbTester dbTester

    @Autowired
    Sql gSql


    def setup() {

        changeLogCleanup=new ChangeLogCleanup(dbAccess)
        dbTester.cleanInsert("datachangelog")

    }

    def "should delete the changelogs with status 1"() {

        given:
        String sql="select * from datachangelog where status=1"
        gSql.execute("update datachangelog set status=1")
        assert gSql.rows(sql).size()==3

        when:
        changeLogCleanup.cleanupChangeLog()

        then:
        gSql.rows(sql).size()==0

    }



}