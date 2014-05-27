package org.cghr.security.service

import com.google.gson.Gson
import groovy.transform.CompileStatic
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.security.exception.NoSuchUserFound
import org.cghr.security.exception.ServerNotFoundException
import org.cghr.security.model.User

@CompileStatic
class UserService {

    DbAccess dbAccess
    DbStore dbStore
    OnlineAuthService onlineAuthService

    UserService(DbAccess dbAccess, DbStore dbStore, OnlineAuthService onlineAuthService) {
        this.dbAccess = dbAccess
        this.dbStore = dbStore
        this.onlineAuthService = onlineAuthService
    }


    boolean isValid(User user, String hostname) {

        try {
            Map userRespFromServer = onlineAuthService.authenticate(user, hostname);
            cacheUserLocally(userRespFromServer)
        }

        catch (ServerNotFoundException ex) {
            println 'Server Not Found'
        }
        catch (NoSuchUserFound ex) {
            return false
        }

        finally {
            return isValidLocalUser(user)
        }
    }

    boolean isValidLocalUser(User user) {

        println 'inside is valid local user'
        Map userData = getUserAsMap(user)
        def isValid = userData.isEmpty() ? false : (userData.password == user.password)
        println 'isvalid local user ' + isValid
        isValid
    }

    void cacheUserLocally(Map user) {


        dbStore.saveOrUpdate([id: user.id, username: user.username, password: user.password, role: ((Map) user.role).title], 'user')
    }

    String getUserJson(User user) {


        dbAccess.getRowAsJson("select * from user where username=?", [user.username])
    }

    String getId(User user) {

        def row = getUserAsMap(user)
        row.id
    }

    String getUserCookieJson(User user) {

        Map<String, String> row = getUserAsMap(user)
        def userMap = [id: row.id, username: row.username, password: row.password, role: [title: row.role, bitMask: getBitMask(row.get('role'))]]
        new Gson().toJson(userMap)
    }

    Integer getBitMask(String role) {

        println 'role ' + role

        switch (role) {

            case 'public':
                return 1;
            case 'user':
                return 2;
            case 'manager':
                return 4;
            case 'coordinator':
                return 8;
            case 'admin':
                return 16;
        }
    }

    void saveAuthToken(String authtoken, User user) {

        def row = getUserAsMap(user)
        def dataToSave = [token: authtoken, username: user.username, role: row.role]

        dbStore.saveOrUpdate(dataToSave, "authtoken")
    }

    void logUserAuthStatus(User user, String status) {

        dbStore.saveOrUpdate([username: user.username, status: status], "userlog")
    }

    Map getUserAsMap(User user) {

        dbAccess.getRowAsMap("select * from user where username=?", [user.username])
    }

    boolean isValidToken(String token) {

        dbAccess.hasRows("select * from authtoken where token=?", [token])
    }
}
