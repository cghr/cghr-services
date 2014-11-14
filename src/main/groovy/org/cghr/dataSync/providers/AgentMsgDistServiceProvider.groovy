package org.cghr.dataSync.providers

import groovy.transform.TupleConstructor
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.dataSync.service.AgentMsgDistService

/**
 * Created by ravitej on 14/11/14.
 */
@TupleConstructor
class AgentMsgDistServiceProvider {

    DbAccess dbAccess
    DbStore dbStore

    def provide() {
        new AgentMsgDistService(dbStore, dbAccess)
    }


}
