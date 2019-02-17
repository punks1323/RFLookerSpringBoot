package com.abcapps.serviceimple;

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
import com.abcapps.component.AESMask;
import com.abcapps.exception.EmailIdAlreadyExists;
import com.abcapps.exception.MobileNoAlreadyExists;
import com.abcapps.exception.PasswordNotMatchException;
import com.abcapps.repo.MobileRepo;
import com.abcapps.repo.RoleRepository;
import com.abcapps.repo.UserRepository;
import com.abcapps.service.MailService;
import com.abcapps.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

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

	@Autowired
	private AESMask aesMask;

	@Override
	public User saveUser(final User user)
			throws EmailIdAlreadyExists, PasswordNotMatchException, MobileNoAlreadyExists {
		log.debug("Registration request for " + user.getEmailId() + "\t" + user.getMobileNo());

		if (!user.getPassword().equals(user.getConfirmPassword())) {
			throw new PasswordNotMatchException();
		}

		User anyUser = userRepo.findByEmailId(user.getEmailId());
		if (anyUser != null) {
			log.warn("User already registered with : " + user.getEmailId());
			throw new EmailIdAlreadyExists();
		}

		anyUser = userRepo.findByMobile(user.getMobile());
		if (anyUser != null) {
			log.warn("User already registered with Mobile no : " + user.getMobileNo());
			throw new MobileNoAlreadyExists();
		}

		user.setEmailIdVerified(false);
		user.setEmailIdUuid(UUID.randomUUID().toString().replaceAll("-",""));
		user.setMobileNoVerified(false);
		user.setRegistrationDate(new Date());
		user.setActive(false);
		user.getMobile().setLastAccess(new Date());

		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		HashSet<Role> roles = new HashSet<Role>();
		roles.add(roleRepository.findByName("ROLE_USER"));
		user.setRoles(roles);

		Runnable emailRunnable = new Runnable() {

			@Override
			public void run() {
				try {

					String url = "http://localhost:8080/auth/verifyEmail/" + aesMask.encode(user.getEmailIdUuid());
					boolean sendMail = mailService.sendMail(user.getEmailId(),
							"Please click on the below link to verify your email id \n " + url,
							"Please verify your email id");
					if (sendMail) {
						log.debug("Mail sent to..." + user.getEmailId());
					} else {
						log.warn("Mail not sent to..." + user.getEmailId());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};

		new Thread(emailRunnable).start();

		log.warn("USER REGISTERED :: " + user);
		return userRepo.save(user);
	}

	@Override
	public boolean verifyEmailByEmailUUID(String uuid) {
		try {
			
			String g="pankaj sharma";
			String e=aesMask.encode(g);
			log.info("ENC  ::::: "+e);
			log.info("DEC  ::::: "+aesMask.decode(e));
			User user = userRepo.findByEmailIdUuid(aesMask.decode(uuid));
			if (user != null) {
				user.setEmailIdVerified(true);
				user.setActive(true);
				user = userRepo.save(user);
				System.out.println(user);
				return true;
			} else
				return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

}
