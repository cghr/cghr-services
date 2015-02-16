package org.cghr.commons.cmd

import groovy.sql.Sql
import org.cghr.commons.db.DbAccess
import org.cghr.startupTasks.CommandExecutor
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import spock.lang.Specification

/**
 * Created by ravitej on 16/2/15.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader)
class CommandExecutorSpec extends Specification {

    @Autowired
    Sql gSql

    @Autowired
    DbTester dbTester

    @Autowired
    DbAccess dbAccess
    @Autowired
    ArrayList commandConfig

    CommandExecutor commandExecutor


    def setup() {

        commandExecutor=new CommandExecutor(commandConfig,dbAccess)
        dbTester.cleanInsert("country,user,authtoken,userlog,inbox,outbox,datachangelog,filechangelog,sales")
    }

    def "should execute the given commands"() {

        given:
        List commandList = ["cleanup"]

        when:
        commandExecutor.execCommandList(commandList)

        then:
        numberOfRows(table) == result


        where:
        table           || result
        "country"       || 0
        "user"          || 5
        "authtoken"     || 0
        "inbox"         || 0
        "outbox"        || 0
        "datachangelog" || 3
        "filechangelog" || 3
        "sales"         || 0

    }
    def "should execute all configured commands in database"(){

        given:
        dbTester.cleanInsert("command")
        assert gSql.rows("select * from command").size()==1

        when:
        commandExecutor.execConfiguredCommands()

        then:
        numberOfRows(table) == result


        where:
        table           || result
        "country"       || 0
        "user"          || 5
        "authtoken"     || 0
        "inbox"         || 0
        "outbox"        || 0
        "datachangelog" || 3
        "filechangelog" || 3
        "sales"         || 0



    }

    int numberOfRows(String table) {
        gSql.firstRow("select count(*) count from "+table).count
    }
}
