package org.cghr.dataSync.controller

import groovy.sql.Sql
import org.cghr.GenericGroovyContextLoader
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Created by ravitej on 5/5/14.
 */
@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class SyncIntegrationSpec extends Specification {

    @Autowired
    Sql gSql
    @Autowired
    SyncService sync
    @Autowired
    DbTester dbTester

    def setupSpec() {


    }

    def setup() {

        dbTester.clean('user')
        dbTester.clean('inbox')
        dbTester.clean('outbox')
        dbTester.clean('country')
        dbTester.clean('authtoken')

        dbTester.cleanInsert('datachangelog')

    }

    def "should download and upload data to a mock Server"() {

        given:
        gSql.execute("insert into user(id,username,password,role) values(?,?,?,?)", [15, 'user1', 'password', 'manager'])
        //Make an entry in authtoken as Manager
        gSql.execute("insert into authtoken(id,token,username,role) values(?,?,?,?)", [1, "fakeToken", "user1", "manager"])

        when:
        sync.synchronize()


        then:
        gSql.rows("select * from country").size() == 3
        gSql.rows("select * from datachangelog where status is null").size() == 0
        gSql.rows("select * from outbox").size() == 2


    }


}