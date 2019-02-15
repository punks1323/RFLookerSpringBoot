package com.abcapps.service;

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.abcapps.Entities.Role;
import com.abcapps.Entities.User;
import com.abcapps.exception.EmailIdAlreadyExists;
import com.abcapps.exception.MobileNoAlreadyExists;
import com.abcapps.exception.PasswordNotMatchException;
import com.abcapps.repo.MobileRepo;
import com.abcapps.repo.RoleRepository;
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

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	/**
	 * Registers a user,
	 * 
	 * @throws MobileNoAlreadyExists
	 * 
	 * @params {@link User} new user object returns null if passwords not match,
	 *         user with email already exists, otherwise user details if registered
	 *         successfully.
	 */
	@Override
	public User saveUser(User user) throws EmailIdAlreadyExists, PasswordNotMatchException, MobileNoAlreadyExists {
		logger.debug("Registration request for " + user.getEmailId() + "\t" + user.getMobileNo());

		if (!user.getPassword().equals(user.getConfirmPassword())) {
			throw new PasswordNotMatchException();
		}

		User anyUser = userRepo.findByEmailId(user.getEmailId());
		if (anyUser != null) {
			logger.warn("User already registered with : " + user.getEmailId());
			throw new EmailIdAlreadyExists();
		}

		anyUser = userRepo.findByMobile(user.getMobile());
		if (anyUser != null) {
			logger.warn("User already registered with Mobile no : " + user.getMobileNo());
			throw new MobileNoAlreadyExists();
		}

		user.setEmailIdVerified(false);
		user.setEmailIdUuid(UUID.randomUUID().toString());
		user.setMobileNoVerified(false);
		user.setRegistrationDate(new Date());

		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		HashSet<Role> roles = new HashSet<Role>();
		roles.add(roleRepository.findByName("ROLE_USER"));
		user.setRoles(roles);

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
