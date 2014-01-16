package org.cghr.security.model

class User {

	int id
	String username
	String password
	String role
	String status

	boolean isEmpty() {
		return this.username==null
	}
}
