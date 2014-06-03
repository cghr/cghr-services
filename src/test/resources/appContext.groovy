import groovy.sql.Sql
import org.apache.tomcat.jdbc.pool.DataSource
import org.cghr.chart.AngularChartDataModel
import org.cghr.commons.db.CleanUp
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.commons.file.FileSystemStore
import org.cghr.dataSync.commons.AgentProvider
import org.cghr.dataSync.commons.SyncRunner
import org.cghr.dataSync.service.SyncUtil
import org.cghr.dataViewModel.DataModelUtil
import org.cghr.dataViewModel.DhtmlxGridModelTransformer
import org.cghr.security.controller.Auth
import org.cghr.security.controller.PostAuth
import org.cghr.security.service.OnlineAuthService
import org.cghr.security.service.UserService
import org.cghr.startupTasks.DbImport
import org.cghr.startupTasks.DirCreator
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
    mvc.'annotation-driven'()
    mvc.'interceptors'() {
        mvc.'mapping'('path': '/api/GridService/**') {
            bean('class': 'org.cghr.security.controller.AuthInterceptor')
        }
    }
    //Todo Add project specific Services

    String userHome = System.getProperty('user.home') + '/'
    String appPath = 'dummyPath/' //Todo System.getProperty('basePath')
    String server = 'http://barshi.vm-host.net:8080/hcServer/'
    serverBaseUrl(String, server)

    //Todo Database Config
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
    //Todo  Project Entities
    dataStoreFactory(HashMap, [country: 'id', inbox: 'id', outbox: 'id', memberImage: 'memberId', filechangelog: 'id'])
    dbStore(DbStore, gSql = gSql, dataStoreFactory = dataStoreFactory)

    //Todo File Store Config
    fileStoreFactory(HashMap,
            [memberImage: [
                    memberConsent: appPath + "repo/images/consent",
                    memberPhotoId: appPath + "repo/images/photoId",
                    memberPhoto: appPath + "repo/images/photo"
            ]])
    fileSystemStore(FileSystemStore, fileStoreFactory = fileStoreFactory, dbStore = dbStore)
    dt(DbTester, dataSource = dataSource) //Todo Only For unit Testing

    //Data Model for reports
    transformer(DhtmlxGridModelTransformer, gSql = gSql)
    dataModelUtil(DataModelUtil, transformer = transformer, dbAccess = dbAccess)

    //Todo Security
    serverAuthUrl(String, "http://localhost:8089/app/api/security/auth")
    httpClientParams()
    restTemplate(RestTemplate)
    onlineAuthService(OnlineAuthService, serverAuthUrl = serverAuthUrl, restTemplate = restTemplate)
    userService(UserService, dbAccess = dbAccess, dbStore = dbStore, onlineAuthService = onlineAuthService)
    postAuth(PostAuth, userService = userService)
    auth(Auth)

    //Todo Startup Tasks
    dbImport(DbImport, sqlDir = appPath + 'sqlImport', gSql = gSql)
    dirCreator(DirCreator, [
            appPath + 'repo/images/consent',
            appPath + 'repo/images/photo',
            appPath + 'repo/images/photoId'
    ])

    //Todo Data Synchronization
    String appName = 'hc'
    syncUtil(SyncUtil, restTemplate = restTemplate, baseIp = '192.168.0.', startNode = 100, endNode = 120, port = 8080, pathToCheck = 'api/status/manager', appName = appName)
    agentProvider(AgentProvider, gSql = gSql, dbAccess = dbAccess, dbStore = dbStore, restTemplate = restTemplate, changelogChunkSize = 20,
            serverBaseUrl = 'http://demo1278634.mockable.io/',
            downloadInfoPath = 'api/sync/downloadInfo',
            downloadDataBatchPath = 'api/data/dataAccessBatchService/',
            uploadPath = 'api/data/dataStoreBatchService',
            awakeFileManagerPath = 'app/AwakeFileManager',
            fileStoreFactory = fileStoreFactory,
            userHome = userHome,
            syncUtil = syncUtil)
    syncRunner(SyncRunner, agentProvider = agentProvider)

    //Chart Data Model Services
    angularChartDataModel(AngularChartDataModel, dbAccess = dbAccess)

    //Todo Maintenance Tasks
    cleanup(CleanUp, dbAccess = dbAccess, excludedEntities = 'user,area')

}