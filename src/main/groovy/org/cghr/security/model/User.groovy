package org.cghr.security.model

class User {

    String id
    String username
    String password
    String role
    String status

    boolean isEmpty() {
        return this.username == null
    }
}
