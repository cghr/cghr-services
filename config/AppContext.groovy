import groovy.sql.Sql
import org.apache.tomcat.jdbc.pool.DataSource
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.dataViewModel.DataModelUtil
import org.cghr.dataViewModel.DhtmlxGridModelTransformer
import org.cghr.security.controller.Auth
import org.cghr.security.service.OnlineAuthService
import org.cghr.security.service.UserService
import org.cghr.test.db.DbTester
import org.springframework.web.client.RestTemplate

beans {

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
    dataStoreFactory(HashMap, [country: 'id', inbox: 'id', outbox: 'id', memberImage: 'memberId'])
    dbStore(DbStore, gSql = gSql, dataStoreFactory = dataStoreFactory)
    dt(DbTester, dataSource = dataSource)

    //Integration Test Beans
    transformer(DhtmlxGridModelTransformer, gSql = gSql)
    dataModelUtil(DataModelUtil, transformer = transformer, dbAccess = dbAccess)
    serverAuthUrl(String, "http://localhost:8089/app/api/security/auth")
    restTemplate(RestTemplate)
    onlineAuthService(OnlineAuthService, serverAuthUrl = serverAuthUrl, restTemplate = restTemplate)
    userService(UserService, dbAccess = dbAccess, dbStore = dbStore, onlineAuthService = onlineAuthService)
    auth(Auth, userService = userService)
    //Ends

}