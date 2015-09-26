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
        fileChangelogs.each { uploadAFile(it) }
    }

    void uploadAFile(Map<String, Integer> fileInfo) {

        try {
            agentService.with {
                uploadFile(fileInfo)
                fileUploadSuccessful(fileInfo.id)
            }
        }
        catch (Exception e) {
            e.printStackTrace()
        }


    }

}
