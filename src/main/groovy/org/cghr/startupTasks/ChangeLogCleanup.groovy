package org.cghr.startupTasks
import groovy.transform.TupleConstructor
import org.cghr.commons.db.DbAccess

import javax.annotation.PostConstruct
/**
 * Created by ravitej on 15/2/15.
 */
@TupleConstructor
class ChangeLogCleanup {


    DbAccess dbAccess


    @PostConstruct
    void cleanupChangeLog() {

        dbAccess.removeData("datachangelog","status","1")
    }


}
