package org.cghr.security.service

import com.google.gson.Gson
import org.apache.http.conn.HttpHostConnectException
import org.cghr.commons.db.DbAccess
import org.cghr.commons.db.DbStore
import org.cghr.security.exception.NoSuchUserFound
import org.cghr.security.exception.ServerNotFoundException
import org.cghr.security.model.User

class UserService {

    DbAccess dbAccess
    DbStore dbStore
    OnlineAuthService onlineAuthService

    UserService(DbAccess dbAccess, DbStore dbStore, OnlineAuthService onlineAuthService) {
        this.dbAccess = dbAccess
        this.dbStore = dbStore
        this.onlineAuthService = onlineAuthService
    }


    def boolean isValid(User user,String hostname) {

        try {
            User userRespFromServer = onlineAuthService.authenticate(user,hostname)
            cacheUserLocally(userRespFromServer)
        }
        catch (ServerNotFoundException ex)
        {
            println 'Server Not Found'
        }
        catch (NoSuchUserFound ex) {
            println 'No Such User Found on Server'
            return false
        }
        catch (Exception ex)
        {
            println "Unexpected Exception"
            println ex
        }

        finally {
            return isValidLocalUser(user)
        }
    }

    def boolean isValidLocalUser(User user) {

        Map userData = getUserAsMap(user)
        userData.isEmpty() ? false : (userData.password.equals(user.password))
    }

    def cacheUserLocally(User user) {


        dbStore.saveOrUpdate([id: user.id, username: user.username, password: user.password, role: user.role, status: user.status], 'user')
    }

    def String getUserJson(User user) {


        dbAccess.getRowAsJson("select * from user where username=?", [user.username])
    }

    def String getId(User user) {

        def row = getUserAsMap(user)
        row.id
    }

    def String getUserCookieJson(User user) {

        def row = getUserAsMap(user)
        def userMap = [username: row.username, role: [title: row.role, bitMask: getBitMask(row.role)]]
        new Gson().toJson(userMap)
    }

    def int getBitMask(String role) {

        switch (role) {

            case 'public':
                return 1;
            case 'user':
                return 2;
            case 'manager':
                return 3;
            case 'coordinator':
                return 4;
            case 'admin':
                return 5;
        }
    }

    def saveAuthToken(String authtoken, User user) {

        def row = getUserAsMap(user)
        def dataToSave = [token: authtoken, username: user.username, role: row.role]

        dbStore.saveOrUpdate(dataToSave, "authtoken")
    }

    def logUserAuthStatus(User user, String status) {

        dbStore.saveOrUpdate([username: user.username, status: status], "userlog")
    }

    def getUserAsMap(User user) {

        dbAccess.getRowAsMap("select * from user where username=?", [user.username])
    }

    def isValidToken(String token) {

        dbAccess.hasRows("select * from authtoken where token=?", [token])
    }
}
