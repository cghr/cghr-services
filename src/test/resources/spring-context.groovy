import groovy.sql.Sql
import org.apache.tomcat.jdbc.pool.DataSource
import org.cghr.commons.db.CleanUp
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.commons.file.FileSystemStore
import org.cghr.dataSync.commons.SyncRunner
import org.cghr.dataSync.providers.*
import org.cghr.dataSync.service.SyncUtil
import org.cghr.security.controller.Auth
import org.cghr.security.controller.AuthInterceptor
import org.cghr.security.controller.PostAuth
import org.cghr.security.controller.RequestParser
import org.cghr.security.service.OnlineAuthService
import org.cghr.security.service.UserService
import org.cghr.startupTasks.DbImport
import org.cghr.startupTasks.DirCreator
import org.cghr.startupTasks.MetaClassEnhancement
import org.cghr.test.db.DbTester
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.commons.CommonsMultipartResolver

beans {
    xmlns([context: 'http://www.springframework.org/schema/context'])
    xmlns([mvc: 'http://www.springframework.org/schema/mvc'])


    context.'component-scan'('base-package': 'org.cghr.commons.web.controller')
    context.'component-scan'('base-package': 'org.cghr.dataSync.controller')
    context.'component-scan'('base-package': 'org.cghr.security.controller')
    context.'component-scan'('base-package': 'org.cghr.survey.controller')

    mvc.'annotation-driven'()
    mvc.'interceptors'() {
        mvc.'mapping'('path': '/api/GridService/**') {
            bean('class': 'org.cghr.security.controller.AuthInterceptor')
        }
    }
    multiPartResolver(CommonsMultipartResolver)
//    contentNegotiationViewResolver(ContentNegotiatingViewResolver, {
//        mediaTypes = [json: 'application/json']
//    })
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
                    memberPhoto  : appPath + "repo/images/photo"
            ]])
    fileSystemStore(FileSystemStore, fileStoreFactory = fileStoreFactory, dbStore = dbStore)
    dt(DbTester, dataSource = dataSource) //Todo Only For unit Testing


    //Todo Security
    serverAuthUrl(String, "http://localhost:8089/app/api/security/auth")
    httpClientParams()
    restTemplate(RestTemplate)
    onlineAuthService(OnlineAuthService, serverAuthUrl = serverAuthUrl, restTemplate = restTemplate)
    userService(UserService, dbAccess = dbAccess, dbStore = dbStore, onlineAuthService = onlineAuthService)
    postAuth(PostAuth)
    auth(Auth)
    requestParser(RequestParser)
    authInterceptor(AuthInterceptor)

    //Todo Startup Tasks  - Metaclass Enhancement
    metaClassEnhancement(MetaClassEnhancement)
    dbImport(DbImport, sqlDir = appPath + 'sqlImport', gSql = gSql)
    dirCreator(DirCreator, [
            appPath + 'repo/images/consent',
            appPath + 'repo/images/photo',
            appPath + 'repo/images/photoId'
    ])

    //Todo Data Synchronization
    String appName = 'hc'
    syncUtil(SyncUtil, dbAccess = dbAccess, restTemplate = restTemplate, baseIp = '192.168.0.', startNode = 100, endNode = 120, port = 8080, pathToCheck = 'api/status/manager', appName = appName)

    agentDownloadServiceProvider(AgentDownloadServiceProvider, dbAccess = dbAccess, dbStore = dbStore, restTemplate = restTemplate,
            serverBaseUrl = 'http://demo1278634.mockable.io/',
            downloadInfoPath = 'api/sync/downloadInfo',
            downloadDataBatchPath = 'api/data/dataAccessBatchService/',
            syncUtil = syncUtil)

    agentFileUploadServiceProvider(AgentFileUploadServiceProvider, dbAccess = dbAccess, dbStore = dbStore, serverBaseUrl = 'http://demo1278634.mockable.io/',
            fileStoreFactory = fileStoreFactory,
            awakeFileManagerPath = 'app/AwakeFileManager',
            remoteFileRepo='hcDemo/repo/images/')

    agentMsgDistServiceProvider(AgentMsgDistServiceProvider, dbAccess = dbAccess, dbStore = dbStore)

    agentUploadServiceProvider(AgentUploadServiceProvider, dbAccess = dbAccess, dbStore = dbStore, restTemplate = restTemplate, changelogChunkSize = 20,
            serverBaseUrl = 'http://demo1278634.mockable.io/',
            uploadPath = 'api/data/dataStoreBatchService',
            syncUtil = syncUtil)

    agentServiceProvider(AgentServiceProvider, agentDownloadServiceProvider,
            agentFileUploadServiceProvider,
            agentMsgDistServiceProvider,
            agentUploadServiceProvider)

    agentProvider(AgentProvider, agentServiceProvider = agentServiceProvider)
    syncRunner(SyncRunner, agentProvider = agentProvider)


    //Todo Maintenance Tasks
    cleanup(CleanUp, dbAccess = dbAccess, excludedEntities = "user,area")

    //Todo JsonSchema Path
    String prodPath = new File('./assets/jsonSchema').getCanonicalPath()
    devJsonSchemaPath(String, "")
    prodJsonSchemaPath(String, "")

    ipAddressPattern(String, "xyz.abc")
    gpsSocketPort(Integer, 4444)


}