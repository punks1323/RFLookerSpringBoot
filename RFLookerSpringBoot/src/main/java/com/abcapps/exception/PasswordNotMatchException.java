package com.abcapps.exception;

public class PasswordNotMatchException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PasswordNotMatchException() {
		super("Passwords are different");
	}
}
