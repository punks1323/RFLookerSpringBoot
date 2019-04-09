package com.abcapps.exception;

public class EmailIdAlreadyExists extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EmailIdAlreadyExists(){
		super("Email id is already registered.");
	}

}
