package org.cghr.dataSync.service

import groovy.transform.TupleConstructor
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.springframework.web.client.RestTemplate

/**
 * Created by ravitej on 2/7/14.
 */
@TupleConstructor
class AgentDownloadService {

    DbAccess dbAccess
    DbStore dbStore
    String syncServerDownloadInfoUrl
    String syncServerDownloadDataBatchUrl
    RestTemplate restTemplate


    List<Map> getDownloadInfo() {

        restTemplate.getForObject(syncServerDownloadInfoUrl, List.class)
    }


    void saveDownloadInfo(List<Map> list) {

        dbStore.saveOrUpdateFromMapList(list, 'inbox')

    }

    List<Map> getInboxMessagesToDownload() {

        dbAccess.rows("select * from inbox where impStatus is null", [])

    }

    void downloadAndImport(Map<String, String> message) {

        List data = getDownloadedData(message)
        importData(data, message.datastore)
    }

    List getDownloadedData(Map message) {

        String url = getDownloadBatchUrl(message)
        restTemplate.getForObject(url, List.class)
    }

    String getDownloadBatchUrl(Map message) {

        syncServerDownloadDataBatchUrl + message.datastore + '/' + message.ref + '/' + message.refId
    }

    void importData(List list, String datastore) {

        list.each { Map data ->
            if (data.timelog)
                data.remove("timelog")
        }
        list.each { dbStore.saveOrUpdate(it, datastore) }
    }


    void importSuccessful(Map message) {

        dbStore.saveOrUpdate([id: message.id, impStatus: 1], 'inbox')
    }


}
