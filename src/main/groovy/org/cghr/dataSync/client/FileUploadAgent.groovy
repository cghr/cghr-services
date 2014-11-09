package org.cghr.dataSync.client

import groovy.transform.TupleConstructor
import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.AgentService

/**
 * Created by ravitej on 8/5/14.
 */
@TupleConstructor
class FileUploadAgent implements Agent {

    AgentService agentService

    @Override
    void run() {

        List<Map> fileChangelogs = agentService.getFileChangelogs()
        uploadFiles(fileChangelogs)
    }

    void uploadFiles(List<Map> fileChangelogs) {

        fileChangelogs.each { uploadAFile(it) }

    }

    void uploadAFile(Map fileInfo) {

        try {
            agentService.uploadFile(fileInfo)
            agentService.fileUploadSuccessful(fileInfo.id)
        }
        catch (Exception e) {
            e.printStackTrace();
            println 'error  uploading the file'
        }


    }

}
