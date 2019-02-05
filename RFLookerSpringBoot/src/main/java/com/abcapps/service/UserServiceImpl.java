package com.abcapps.service;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.abcapps.Entities.User;
import com.abcapps.exception.EmailIdAlreadyExists;
import com.abcapps.exception.PasswordNotMatchException;
import com.abcapps.repo.MobileRepo;
import com.abcapps.repo.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	UserRepository userRepo;

	@Autowired
	MobileRepo mobileRepo;

	@Autowired
	MailService mailService;

	@Override
	public User saveUser(User user) throws EmailIdAlreadyExists, PasswordNotMatchException {
		if (!user.getPassword().equals(user.getConfirmPassword())) {
			throw new PasswordNotMatchException();
		}

		User anyUser = userRepo.findByEmailId(user.getEmailId());

		if (anyUser != null)
			throw new EmailIdAlreadyExists();

		user.setEmailIdVerified(false);
		user.setEmailIdUuid(UUID.randomUUID().toString());
		user.setMobileNoVerified(false);
		user.setRegistrationDate(new Date());

		User oldUser = userRepo.findByMobile(user.getMobile());
		if (oldUser != null) {
			oldUser.setMobile(null);
			userRepo.save(oldUser);
			logger.warn("A user was already registered with this IMEI, old user mobile set to null");
		}

		user.setMobile(user.getMobile());

		user = userRepo.save(user);

		String url = "http://localhost:8080/auth/verifyEmail/" + user.getEmailIdUuid();
		boolean sendMail = mailService.sendMail(user.getEmailId(),
				"Please click on the below link to verify your email id \n " + url, "Please verify your email id");
		if (sendMail) {
			logger.debug("Mail sent...");
		} else {
			logger.warn("Mail not sent...");
		}

		logger.warn("USER REGISTERED :: " + user);
		return user;
	}

	@Override
	public boolean verifyEmailByEmailUUID(String uuid) {
		User user = userRepo.findByEmailIdUuid(uuid);
		if (user != null) {
			user.setEmailIdVerified(true);
			user = userRepo.save(user);
			System.out.println(user);
			return true;
		} else
			return false;
	}

}
