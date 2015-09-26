package org.cghr.dataSync.providers

import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j
import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.dataSync.service.AgentFileUploadservice
import org.cghr.dataSync.service.SyncUtil

/**
 * Created by ravitej on 14/11/14.
 */
@Log4j
@TupleConstructor
class AgentFileUploadServiceProvider {

    DbAccess dbAccess
    DbStore dbStore
    String serverBaseUrl
    Map fileStoreFactory
    String awakeFileManagerPath
    String remoteFileRepo
    SyncUtil syncUtil

    def provide() {
        AwakeFileSession awakeFileSession = buildAwakeFileSession()
        new AgentFileUploadservice(dbAccess, dbStore, awakeFileSession, fileStoreFactory, remoteFileRepo)

    }


    AwakeFileSession buildAwakeFileSession() {

        String username = "demo";
        char[] password = ['d', 'e', 'm', 'o']
        String syncServerBaseUrl = syncUtil.syncServerBaseUrl(serverBaseUrl)

        // Create the Awake FILE Session to the remote server:
        try {
            return new AwakeFileSession(syncServerBaseUrl + awakeFileManagerPath, username,
                    password);

        }
        catch (Exception e) {
            e.printStackTrace()
            log.error('Awake File Manager  Not Found on the Sync Server or Sync Server not Available')
        }
    }

}
