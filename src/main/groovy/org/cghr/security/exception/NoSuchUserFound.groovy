package org.cghr.security.exception

import groovy.transform.CompileStatic

@CompileStatic
class NoSuchUserFound extends Exception {
    NoSuchUserFound() {
        super("No such User Found Exception")
    }

    NoSuchUserFound(String msg) {
        super(msg)
    }
}
