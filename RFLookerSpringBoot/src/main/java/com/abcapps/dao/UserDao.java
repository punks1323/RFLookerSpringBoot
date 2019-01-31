package com.abcapps.dao;

import com.abcapps.Entities.User;
import com.abcapps.dto.UserDTO;
import com.abcapps.exception.EmailIdAlreadyExists;
import com.abcapps.exception.PasswordNotMatchException;

public interface UserDao {
	User saveUser(UserDTO userDto) throws EmailIdAlreadyExists, PasswordNotMatchException;
	boolean verifyEmailByEmailUUID(String uuid);
}
