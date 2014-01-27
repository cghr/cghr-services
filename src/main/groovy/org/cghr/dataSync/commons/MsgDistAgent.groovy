package org.cghr.dataSync.commons

import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore

class MsgDistAgent implements Agent {

    DbAccess dbAccess
    DbStore dbStore

    MsgDistAgent(DbAccess dbAccess, DbStore dbStore) {
        this.dbAccess = dbAccess
        this.dbStore = dbStore
    }

    public void run() {

        List distFiles = getDistInfo()
        distributeMessages(distFiles)

    }

    void distributeMessages(List list) {


        list.each {
            row ->
                List recepients = row.distList.split(",") as List
                recepients.each {

                    recepient ->


                        dbStore.saveOrUpdate([message: row.message, recepient: recepient], 'outbox')

                }


        }

    }

    List getDistInfo() {
        return dbAccess.getRowsAsListOfMaps('select id,message,distList from inbox where distStatus is null', null)
    }
}
