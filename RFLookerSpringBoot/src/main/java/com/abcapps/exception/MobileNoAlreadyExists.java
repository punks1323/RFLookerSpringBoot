package com.abcapps.exception;

public class MobileNoAlreadyExists extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MobileNoAlreadyExists(){
		super("Mobile no already registered.");
	}

}
