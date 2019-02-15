package com.abcapps.service;

import com.abcapps.Entities.User;
import com.abcapps.exception.EmailIdAlreadyExists;
import com.abcapps.exception.MobileNoAlreadyExists;
import com.abcapps.exception.PasswordNotMatchException;

public interface UserService {
	User saveUser(User user) throws EmailIdAlreadyExists, PasswordNotMatchException, MobileNoAlreadyExists;

	boolean verifyEmailByEmailUUID(String uuid);
}
