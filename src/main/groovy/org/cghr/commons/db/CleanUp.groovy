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

        List tablesToTruncate = []
        List exemptTables = excludedEntities.split(',')
        List allTables = getAllTables()


        exemptTables.each {
            if (allTables.contains(it))
                allTables.remove(it)
        }
        dbAccess.removeData(allTables)
    }

    List getAllTables() {

        List<Map> rows = dbAccess.getRowsAsListOfMaps('show tables', [])
        rows.collect {
            Map row ->
                row.values()[0]
        }

    }


}
