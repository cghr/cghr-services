package org.cghr.security.service

import com.google.gson.Gson
import groovy.transform.CompileStatic
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


    def boolean isValid(User user, String hostname) {

        try {
            Map userRespFromServer = onlineAuthService.authenticate(user, hostname);
            cacheUserLocally(userRespFromServer)
        }
        catch (ServerNotFoundException ex) {
            println 'Server Not Found'
        }
        catch (NoSuchUserFound ex) {
            println 'No Such User Found on Server'
            return false
        }
        catch (Exception ex) {
            println "Unexpected Exception"
            println ex
        }

        finally {
            return isValidLocalUser(user)
        }
    }

    def boolean isValidLocalUser(User user) {

        println 'inside is valid local user'
        Map userData = getUserAsMap(user)
        def isValid = userData.isEmpty() ? false : (userData.password==user.password)
        println 'isvalid local user '+isValid
        isValid
    }

    def cacheUserLocally(Map user) {


        dbStore.saveOrUpdate([id: user.id, username: user.username, password: user.password, role: ((Map)user.role).title], 'user')
    }

    def String getUserJson(User user) {


        dbAccess.getRowAsJson("select * from user where username=?", [user.username])
    }

    def String getId(User user) {

        def row = getUserAsMap(user)
        row.id
    }

    def String getUserCookieJson(User user) {

        Map row = getUserAsMap(user)
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
