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

        List exemptTables = excludedEntities.split(',') as List
        dbAccess.removeData(getAllTables().findAll { !exemptTables.contains(it) })
    }

    List getAllTables() {

        dbAccess.rows('show tables', []).collect {
            it.values()[0]
        }
    }
}
