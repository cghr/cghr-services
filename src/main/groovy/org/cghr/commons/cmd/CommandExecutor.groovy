package org.cghr.commons.cmd

import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j

/**
 * Created by ravitej on 16/2/15.
 */
@Log4j
@TupleConstructor
class CommandExecutor {


    List commandConfig



    boolean execCommand(String commandName){


        Map cmd=commandConfig.find {it.name==commandName}
        def execObj=cmd.refObj
        def execFn=cmd.execFn

        execFn.call(execObj)
        log.info("Command Execution Successful $commandName")
    }

    boolean execCommandList(List<String> commandList){


        commandList.each {
            execCommand(it)
        }

    }




}
