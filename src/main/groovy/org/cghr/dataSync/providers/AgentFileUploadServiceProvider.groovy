package org.cghr.dataSync.providers
import groovy.transform.TupleConstructor
import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.dataSync.service.AgentFileUploadservice
/**
 * Created by ravitej on 14/11/14.
 */
@TupleConstructor
class AgentFileUploadServiceProvider {

    DbAccess dbAccess
    DbStore dbStore
    String serverBaseUrl
    Map fileStoreFactory
    String awakeFileManagerPath

    def provide() {
        AwakeFileSession awakeFileSession = buildAwakeFileSession()
        new AgentFileUploadservice(dbAccess, dbStore, awakeFileSession, fileStoreFactory)

    }


    AwakeFileSession buildAwakeFileSession() {

        String username = "demo";
        char[] password = ['d', 'e', 'm', 'o']

        // Create the Awake FILE Session to the remote server:
        try {
            return new AwakeFileSession(serverBaseUrl + awakeFileManagerPath, username,
                    password);

        }
        catch (Exception e) {
            println 'Awake File Manager  Not Found on the Sync Server or Sync Server not Available'
        }
    }

}
