package org.cghr.dataSync.client

import org.cghr.commons.db.DbStore
import org.cghr.dataSync.commons.Agent
import org.cghr.dataSync.service.DataSyncService

class DownloadOrganizerAgent implements Agent {

    DataSyncService dataSyncService
    DbStore dbStore

    DownloadOrganizerAgent(DataSyncService dataSyncService, DbStore dbStore) {
        this.dataSyncService = dataSyncService
        this.dbStore = dbStore


    }


    void run() {

        saveDownloadInfoToInbox(dataSyncService.getDownloadInfo())
    }

    void saveDownloadInfoToInbox(List<Map> downloadInfo) {

        dbStore.saveOrUpdateFromMapList(downloadInfo, 'inbox')

    }
}
