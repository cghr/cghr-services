package org.cghr.security.service

import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.security.model.User
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.ResourceAccessException

@Log4j
@TupleConstructor
class UserService {

    DbAccess dbAccess
    DbStore dbStore
    OnlineAuthService onlineAuthService
    Map tokenCache


    boolean isValid(User user, String hostname) {

        if (isServerHost(hostname, onlineAuthService.serverAuthUrl))
            return isValidLocalUser(user)

        try {
            Map userResponse = onlineAuthService.authenticate(user)
            cacheUserLocally(userResponse)
        }
        catch (ResourceAccessException ex) {
            log.info 'Offline Mode:Authenticating Locally'
        }
        catch (HttpClientErrorException ex) {
            return false
        }
        finally {
            return isValidLocalUser(user)
        }

    }

    boolean isServerHost(String hostname, String serverAuthUrl) {

        String serverHost = serverAuthUrl.toURL().host
        hostname == serverHost && serverHost != 'localhost'
    }


    boolean isValidLocalUser(User user) {

        Map userData = getUserAsMap(user)
        userData.isEmpty() ? false : (userData.password == user.password)
    }

    void cacheUserLocally(Map user) {

        dbStore.saveOrUpdate([id: user.id, username: user.username, password: user.password, role: ((Map) user.role).title], 'user')
    }

    String getUserJson(User user) {

        dbAccess.firstRow("select * from user where username=?", [user.username]).toJson()
    }

    String getId(User user) {
        getUserAsMap(user).id
    }

    String getUserCookieJson(User user) {

        Map row = getUserAsMap(user)
        def userMap = [id: row.id, username: row.username, password: row.password, role: [title: row.role, bitMask: getBitMask(row.get('role'))]]
        userMap.toJson()
    }

    Integer getBitMask(String role) {
        Map bitMasks = [public: 1, user: 2, manager: 4, coordinator: 8, admin: 16]
        bitMasks."$role"
    }

    void saveAuthToken(String authtoken, User user) {

        def row = getUserAsMap(user)
        def dataToSave = [token: authtoken, username: user.username, role: row.role]
        tokenCache.put(user.username, [token: authtoken, role: row.role])

        dbStore.saveOrUpdate(dataToSave, "authtoken")
    }

    void logUserAuthStatus(User user, String status) {

        dbStore.saveOrUpdate([username: user.username, status: status], "userlog")
    }

    Map getUserAsMap(User user) {

        dbAccess.firstRow("select * from user where username=?", [user.username])
    }

    boolean isValidToken(String token) {

        dbAccess.firstRow("select * from authtoken where token=?", [token]) ? true : false
    }

    boolean isUserAuthorised(String username) {
        tokenCache.get(username)
    }
}
