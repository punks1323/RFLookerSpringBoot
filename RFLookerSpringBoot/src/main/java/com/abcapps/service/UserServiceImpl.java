package com.abcapps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abcapps.Entities.User;
import com.abcapps.dao.UserDao;
import com.abcapps.dto.UserDTO;
import com.abcapps.exception.EmailIdAlreadyExists;
import com.abcapps.exception.PasswordNotMatchException;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;

	@Override
	public User saveUser(UserDTO userDto) throws EmailIdAlreadyExists, PasswordNotMatchException {
		return userDao.saveUser(userDto);
	}

	@Override
	public boolean verifyEmailByEmailUUID(String uuid) {
		return userDao.verifyEmailByEmailUUID(uuid);
	}

}
