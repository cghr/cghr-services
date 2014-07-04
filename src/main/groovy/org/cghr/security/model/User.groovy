package org.cghr.security.model

import com.google.gson.Gson
import groovy.transform.CompileStatic

@CompileStatic
class User {

    String id
    String username
    String password
    String role
    String status

    boolean isEmpty() {
        return this.username == null
    }

    String toJson() {
        Gson gson = new Gson()
        gson.toJson(this)
    }
}
