package org.cghr.commons.db
/**
 * Created by ravitej on 9/5/14.
 */
class CleanUp {

    String excludedEntities
    DbAccess dbAccess

    CleanUp(String excludedEntities, DbAccess dbAccess) {

        this.excludedEntities = excludedEntities
        this.dbAccess = dbAccess
    }

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
