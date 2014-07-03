package org.cghr.dataSync.service

import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore

/**
 * Created by ravitej on 3/7/14.
 */
class AgentMsgDistService {

    DbStore dbStore
    DbAccess dbAccess

    AgentMsgDistService(DbStore dbStore, DbAccess dbAccess) {
        this.dbStore = dbStore
        this.dbAccess = dbAccess
    }

    void importSuccessful(Map message) {

        dbStore.saveOrUpdate([id: message.id, impStatus: 1], 'inbox')
    }

    List<Map> getInboxMessagesToDistribute() {

        dbAccess.rows("select * from inbox where impStatus is not null and distStatus is null", [])
    }

    void distributeMessage(Map message, String recipient) {

        dbStore.saveOrUpdate([datastore: message.datastore, ref: message.ref, refId: message.refId, recipient: recipient], 'outbox')
    }

    void distributeSuccessful(Map message) {
        dbStore.saveOrUpdate([id: message.id, distStatus: 1], 'inbox')
    }

}
