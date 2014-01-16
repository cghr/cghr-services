package org.cghr.security.exception

class NoSuchUserFound extends Exception {
	NoSuchUserFound() {
		super("No such User Found Exception")
	}
	NoSuchUserFound(String msg) {
		supser(msg)
	}
}
