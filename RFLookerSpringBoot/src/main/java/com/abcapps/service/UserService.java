package com.abcapps.service;

import com.abcapps.Entities.User;
import com.abcapps.dto.UserDTO;
import com.abcapps.exception.EmailIdAlreadyExists;
import com.abcapps.exception.PasswordNotMatchException;

public interface UserService {
	User saveUser(UserDTO userDto) throws EmailIdAlreadyExists, PasswordNotMatchException;

	boolean verifyEmailByEmailUUID(String uuid);
}
