package org.cghr.dataSync.commons

import groovy.sql.Sql
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.springframework.web.client.RestTemplate

class AgentProvider {


    Sql gSql
    DbAccess dbAccess
    DbStore dbStore
    RestTemplate restTemplate
    Integer changelogChunkSize
    String serverBaseUrl
    String downloadInfoPath
    String downloadDataBatchPath
    String uploadPath

    static Agent provide(String agentId) {

        return null

    }


}
