package org.cghr.security.exception

import groovy.transform.CompileStatic


@CompileStatic
class ServerNotFoundException extends Exception {

    ServerNotFoundException() {
        super("failed to connect to server exception")
    }

    ServerNotFoundException(String msg) {
        super(msg)
    }
}
