package com.cms.policy.exception;

public class UserNotFoundException extends RuntimeException {
	
	public UserNotFoundException() {
		super("User not found in database");
	}
	
	public UserNotFoundException(String message) {
		super(message);
	}

}
