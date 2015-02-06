import groovy.sql.Sql
import org.apache.tomcat.jdbc.pool.DataSource
import org.cghr.chart.AngularChartModel
import org.cghr.commons.db.CleanUp
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.commons.entity.Entity
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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.commons.CommonsMultipartResolver

beans {
    xmlns([context: 'http://www.springframework.org/schema/context'])
    xmlns([mvc: 'http://www.springframework.org/schema/mvc'])


    context.'component-scan'('base-package': 'org.cghr.commons.web.controller')
    context.'component-scan'('base-package': 'org.cghr.dataSync.controller')
    context.'component-scan'('base-package': 'org.cghr.security.controller')
    context.'component-scan'('base-package': 'org.cghr.survey.controller')
    //Todo project specific controller packages
    //context.'component-scan'('base-package': 'org.cghr.<appName>.controller')

    mvc.'annotation-driven'()
    mvc.'interceptors'() {
        mvc.'mapping'('path': '/api/GridService/**') {
            bean('class': 'org.cghr.security.controller.AuthInterceptor')
        }
    }
    // Http Message Converters. Json-Objects and Vice-versa
    jacksonMapperFactoryBean(Jackson2ObjectMapperFactoryBean)
    httpMsgConverters(MappingJackson2HttpMessageConverter) {
        objectMapper = jacksonMapperFactoryBean
    }

    multipartResolver(CommonsMultipartResolver) {
        maxInMemorySize = 10240
        maxUploadSize = 1024000000
    }

    //Todo Add project specific Services
    String userHome = System.getProperty('userHome')
    String appPath = 'dummyPath/' //Todo System.getProperty('basePath')
    String server = 'http://barshi.vm-host.net:8080/isha/'
    serverBaseUrl(String, server)

    //Todo Database Config
    dataSource(DataSource) { bean ->
        bean.destroyMethod = 'close'
        driverClassName = 'org.h2.Driver'
        url = 'jdbc:h2:mem:specs;database_to_upper=false;mode=mysql'
        //Todo production config
        //url = 'jdbc:h2:~/isha;database_to_upper=false;mode=mysql;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE'
        username = 'sa'
        password = ''
        initialSize = 5
        maxActive = 10
        maxIdle = 5
        minIdle = 2
    }

    gSql(Sql, dataSource = dataSource)
    dbAccess(DbAccess, gSql = gSql)

    //Todo  Project specific Entities
    dataStoreFactory(HashMap, [country: 'id', inbox: 'id', outbox: 'id', memberImage: 'memberId', filechangelog: 'id'])
    dbStore(DbStore, gSql = gSql, dataStoreFactory = dataStoreFactory)
    entity(Entity, dbAccess = dbAccess,
            dbStore = dbStore,
            dataStoreFactory = dataStoreFactory
    )

    //Todo File Store Config
    fileStoreFactory(HashMap,
            [memberImage: [
                    memberConsent: userHome + "hc/repo/images/consent",
                    memberPhotoId: userHome + "hc/repo/images/photoId",
                    memberPhoto  : userHome + "hc/repo/images/photo"
            ]])
    fileSystemStore(FileSystemStore, fileStoreFactory = fileStoreFactory, dbStore = dbStore)
    dt(DbTester, dataSource = dataSource) //Todo Only For unit Testing

    //Todo Security
    tokenCache(HashMap, [:])
    serverAuthUrl(String, "http://localhost:8089/app/api/security/auth")
    httpClientParams()
    httpRequestFactory(HttpComponentsClientHttpRequestFactory) {
        readTimeout = 3000
        connectTimeout = 3000
    }
    restTemplate(RestTemplate, httpRequestFactory)
    onlineAuthService(OnlineAuthService, serverAuthUrl = serverAuthUrl, restTemplate = restTemplate)
    userService(UserService, dbAccess = dbAccess, dbStore = dbStore, onlineAuthService = onlineAuthService, tokenCache = tokenCache)
    postAuth(PostAuth)
    auth(Auth)
    requestParser(RequestParser)
    authInterceptor(AuthInterceptor)

    //Todo Startup Tasks  - Metaclass Enhancement
    metaClassEnhancement(MetaClassEnhancement)
    dbImport(DbImport, sqlDir = appPath + 'sqlImport', gSql = gSql)
    dirCreator(DirCreator, [
            userHome + 'hc/repo/images/consent',
            userHome + 'hc/repo/images/photo',
            userHome + 'hc/repo/images/photoId'
    ])

    //Todo Data Synchronization
    String appName = 'hc'
    syncUtil(SyncUtil, dbAccess = dbAccess, restTemplate = restTemplate, baseIp = '192.168.0.', startNode = 100, endNode = 120, port = 8080, pathToCheck = 'api/sync/status/manager', appName = appName)

    agentDownloadServiceProvider(AgentDownloadServiceProvider, dbAccess = dbAccess, dbStore = dbStore, restTemplate = restTemplate,
            serverBaseUrl = 'http://demo1278634.mockable.io/',//todo
            downloadInfoPath = 'api/sync/downloadInfo',
            downloadDataBatchPath = 'api/data/dataAccessBatchService/',
            syncUtil = syncUtil)

    agentFileUploadServiceProvider(AgentFileUploadServiceProvider, dbAccess = dbAccess, dbStore = dbStore, serverBaseUrl = 'http://demo1278634.mockable.io/',
            fileStoreFactory = fileStoreFactory,
            awakeFileManagerPath = 'AwakeFileManager',
            remoteFileRepo = 'hc/repo/images/',
            syncUtil = syncUtil)

    agentMsgDistServiceProvider(AgentMsgDistServiceProvider, dbAccess = dbAccess, dbStore = dbStore)

    agentUploadServiceProvider(AgentUploadServiceProvider, dbAccess = dbAccess, dbStore = dbStore, restTemplate = restTemplate, changelogChunkSize = 1,
            serverBaseUrl = 'http://demo1278634.mockable.io/',//todo
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

    String prodPath = new File('./assets/jsonSchema').getCanonicalPath()
    devJsonSchemaPath(String, userHome + 'apps/<appName>/ui/src/assets/jsonSchema')
    prodJsonSchemaPath(String, prodPath)

    //Todo ipaddress pattern
    ipAddressPattern(String, "abc.xyz")
    gpsSocketPort(Integer, 4444)

    chartModel(AngularChartModel, dbAccess = dbAccess)

}