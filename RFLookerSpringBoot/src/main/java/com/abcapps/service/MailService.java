package com.abcapps.service;

public interface MailService {
	boolean sendMail(String to, String msg, String subject);
}
