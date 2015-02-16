package org.cghr.startupTasks

import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j
import org.cghr.commons.db.DbAccess

import javax.annotation.PostConstruct

/**
 * Created by ravitej on 16/2/15.
 */
@Log4j
@TupleConstructor
class CommandExecutor {


    List commandConfig
    DbAccess dbAccess


    boolean execCommand(String commandName) {


        Map cmd = commandConfig.find { it.name == commandName }
        def execObj = cmd.refObj
        def execFn = cmd.execFn

        execFn.call(execObj)
        log.info("Command Execution Successful $commandName")
    }

    boolean execCommandList(List<String> commandList) {


        commandList.each {
            execCommand(it)
        }

    }

    List getCommandList() {


        dbAccess.rows("select name from command")
                .collect { it.name }

    }

    @PostConstruct
    void execConfiguredCommands() {

        List commands = getCommandList()
        execCommandList(commands)

    }


}
