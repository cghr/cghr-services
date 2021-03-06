package org.cghr.security.model

import groovy.json.JsonOutput
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
        JsonOutput.toJson(this)
    }
}
