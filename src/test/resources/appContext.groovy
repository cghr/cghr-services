import groovy.sql.Sql
import org.apache.tomcat.jdbc.pool.DataSource
import org.cghr.chart.AngularChartDataModel
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.commons.file.FileSystemStore
import org.cghr.dataSync.commons.AgentProvider
import org.cghr.dataSync.commons.SyncRunner
import org.cghr.dataViewModel.DataModelUtil
import org.cghr.dataViewModel.DhtmlxGridModelTransformer
import org.cghr.security.controller.Auth
import org.cghr.security.service.OnlineAuthService
import org.cghr.security.service.UserService
import org.cghr.test.db.DbTester
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.commons.CommonsMultipartResolver

beans {

    xmlns([context: 'http://www.springframework.org/schema/context'])
    xmlns([mvc: 'http://www.springframework.org/schema/mvc'])

    //Common Services
    context.'component-scan'('base-package': 'org.cghr.commons.web.controller')
    context.'component-scan'('base-package': 'org.cghr.dataSync.controller')
    context.'component-scan'('base-package': 'org.cghr.security.controller')
    context.'component-scan'('base-package': 'org.cghr.survey.controller')
    multiPartResolver(CommonsMultipartResolver)
    //Add Project Specific Services

    String appPath = 'dummyPath' //System.getProperty('basePath')
    //Database Config
    dataSource(DataSource) {
        driverClassName = 'org.h2.Driver'
        url = 'jdbc:h2:mem:specs;database_to_upper=false;mode=mysql'
        username = 'sa'
        password = ''
        initialSize = 5
        maxActive = 10
        maxIdle = 5
        minIdle = 2
    }
    gSql(Sql, dataSource = dataSource)
    dbAccess(DbAccess, gSql = gSql)
    dataStoreFactory(HashMap, [country: 'id', inbox: 'id', outbox: 'id', memberImage: 'memberId', filechangelog: 'id'])
    dbStore(DbStore, gSql = gSql, dataStoreFactory = dataStoreFactory)
    //File Store Config
    fileStoreFactory(HashMap,
            [memberImage: [
                    memberConsent: appPath + "/repo/images/consent",
                    memberPhotoId: appPath + "/repo/images/photoId",
                    memberPhoto: appPath + "/repo/images/photo"
            ]])
    fileSystemStore(FileSystemStore, fileStoreFactory = fileStoreFactory, dbStore = dbStore, userHome = '')
    dt(DbTester, dataSource = dataSource)

    //Data Model for reports
    transformer(DhtmlxGridModelTransformer, gSql = gSql)
    dataModelUtil(DataModelUtil, transformer = transformer, dbAccess = dbAccess)

    //Security
    serverAuthUrl(String, "http://localhost:8089/app/api/security/auth")
    restTemplate(RestTemplate)
    onlineAuthService(OnlineAuthService, serverAuthUrl = serverAuthUrl, restTemplate = restTemplate)
    userService(UserService, dbAccess = dbAccess, dbStore = dbStore, onlineAuthService = onlineAuthService)
    auth(Auth)

    //Data Synchronization
    agentProvider(AgentProvider, gSql = gSql, dbAccess = dbAccess, dbStore = dbStore, restTemplate = restTemplate, changelogChunkSize = 20,
            serverBaseUrl = 'http://demo1278634.mockable.io/',
            downloadInfoPath = 'api/sync/downloadInfo',
            downloadDataBatchPath = 'api/data/dataAccessBatchService/',
            uploadPath = 'api/data/dataStoreBatchService',
            awakeFileManagerPath = 'app/AwakeFileManager',
            fileStoreFactory = fileStoreFactory,
            userHome = userHome)
    syncRunner(SyncRunner, agentProvider = agentProvider)

    angularChartDataModel(AngularChartDataModel,dbAccess=dbAccess)


}