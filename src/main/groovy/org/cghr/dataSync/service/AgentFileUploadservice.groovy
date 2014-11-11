package org.cghr.dataSync.service

import groovy.transform.TupleConstructor
import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore

/**
 * Created by ravitej on 4/7/14.
 */

@TupleConstructor
class AgentFileUploadservice {

    DbAccess dbAccess
    DbStore dbStore
    AwakeFileSession awakeFileSession
    Map fileStoreFactory


    List getFileChangelogs() {
        dbAccess.rows("select * from filechangelog where status is null", [])
    }

    void fileUploadSuccessful(Integer id) {
        dbStore.saveOrUpdate([id: id, status: 1], 'filechangelog')
    }

    void uploadFile(Map fileInfo) {

        String path = fileStoreFactory."$fileInfo.filestore"."$fileInfo.category"
        String category = fileInfo.category
        String remoteFile = "/hcDemo/repo/images/$category" + fileInfo.filename

        File file = new File(path + '/' + fileInfo.filename)
        awakeFileSession.upload(file, remoteFile)

    }


}
