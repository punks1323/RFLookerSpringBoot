package com.abcapps.service;

public interface MailService {
	public boolean sendMail(String to, String msg, String subject);
}
