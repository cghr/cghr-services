package org.cghr.dataSync.commons

import groovy.sql.Sql
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.test.db.DbTester
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by ravitej on 27/1/14.
 */
class MsgDistAgentSpec extends Specification {

    @Shared
    Sql gSql
    @Shared
    DbTester dt

    MsgDistAgent msgDistAgent

    def setupSpec() {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("spring-context.xml")
        gSql = appContext.getBean("gSql")
        dt = appContext.getBean("dt")
    }

    def setup() {

        dt.cleanInsert("inbox")
        dt.clean("outbox")

        DbAccess dbAccess=Stub(){
            getRowsAsListOfMaps('select id,message,distList from inbox where distStatus is null',null) >> gSql.rows('select id,message,distList from inbox where distStatus is null')

        }
        DbStore dbStore=Stub(){
            saveOrUpdate([message:'file1.json',recepient:'1'],'outbox') >> {gSql.executeInsert('insert into outbox(message,recepient) values(?,?)',['file1','1']) }
            saveOrUpdate([message:'file1.json',recepient:'2'],'outbox') >> {gSql.executeInsert('insert into outbox(message,recepient) values(?,?)',['file1','2']) }
            saveOrUpdate([message:'file2.json',recepient:'3'],'outbox') >> {gSql.executeInsert('insert into outbox(message,recepient) values(?,?)',['file2','3']) }
            saveOrUpdate([message:'file2.json',recepient:'4'],'outbox') >> {gSql.executeInsert('insert into outbox(message,recepient) values(?,?)',['file2','4']) }
        }

        msgDistAgent = new MsgDistAgent(dbAccess,dbStore)

    }

    def "should distribute messages to distribution list in each message"() {

        when:
        msgDistAgent.run()

        then:
        gSql.rows("select * from outbox").size()==4
    }

    @Ignore
    def "should do 2"() {

    }

}
