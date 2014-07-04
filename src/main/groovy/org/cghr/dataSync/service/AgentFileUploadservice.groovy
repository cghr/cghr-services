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

        String path = ((Map) fileStoreFactory.get(fileInfo.filestore)).get(fileInfo.fileId)
        //String remoteFile = path + '/' + fileInfo.filename
        String type = ((String) fileInfo.fileId).toLowerCase()
        String remoteFile = ''

        if (type.contains('consent'))
            remoteFile = '/hcDemo/repo/images/consent'
        else if (type.contains('photo'))
            remoteFile = '/hcDemo/repo/images/photo'
        else if (type.contains('photoId'))
            remoteFile = '/hcDemo/repo/images/photoId'

        remoteFile = remoteFile + '/' + fileInfo.filename
        File file = new File(path + '/' + fileInfo.filename)
        println 'file to upload'
        println file
        awakeFileSession.upload(file, remoteFile)

    }


}
