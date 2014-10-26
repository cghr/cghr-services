package org.cghr.commons.db

import groovy.transform.TupleConstructor

/**
 * Created by ravitej on 9/5/14.
 */
@TupleConstructor
class CleanUp {

    String excludedEntities
    DbAccess dbAccess

    
    void cleanupTables() {
        dbAccess.removeData tableListForCleanup
    }

    List getTableListForCleanup() {
        List excludedTables = excludedEntities.split(",") as List
        getAllTables().findAll { !(it in excludedTables) }
    }

    List getAllTables() {

        dbAccess.rows('show tables', []).collect {
            it.values()[0]
        }
    }
}
