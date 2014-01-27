package org.cghr.security.exception


class ServerNotFoundException extends Exception {

    ServerNotFoundException() {
        super("failed to connect to server exception")
    }

    ServerNotFoundException(String msg) {
        super(msg)
    }
}
