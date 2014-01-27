package org.cghr.dataSync.client

import org.awakefw.file.api.client.AwakeFileSession
import org.cghr.commons.db.DbAccess
import org.cghr.dataSync.commons.Agent

class DownloadAgent implements Agent {

    DbAccess dbAccess
    AwakeFileSession awakeFileSession
    String fileBasePath
    DownloadAgent(DbAccess dbAccess,AwakeFileSession awakeFileSession,String fileBasePath) {

        this.dbAccess=dbAccess
        this.awakeFileSession=awakeFileSession
        this.fileBasePath=fileBasePath
    }

    public void run() {


	}

}
