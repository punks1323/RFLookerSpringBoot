package com.abcapps.dao;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abcapps.Entities.Phone;
import com.abcapps.Entities.User;
import com.abcapps.dto.MobileDTO;
import com.abcapps.dto.UserDTO;
import com.abcapps.exception.EmailIdAlreadyExists;
import com.abcapps.exception.PasswordNotMatchException;
import com.abcapps.repo.MobileRepo;
import com.abcapps.repo.UserRepository;
import com.abcapps.service.MailService;

@Service
public class UserDaoImpl implements UserDao {

	Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

	@Autowired
	UserRepository userRepo;

	@Autowired
	MobileRepo mobileRepo;

	@Autowired
	MailService mailService;

	@Override
	public User saveUser(UserDTO userDto) throws EmailIdAlreadyExists, PasswordNotMatchException {
		User newUser;
		User user = userRepo.findByEmailId(userDto.getEmailId());

		if (user != null)
			throw new EmailIdAlreadyExists();
		else if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
			throw new PasswordNotMatchException();
		} else {
			newUser = new User();
			newUser.setName(userDto.getName());
			newUser.setEmailId(userDto.getEmailId());
			newUser.setEmailIdVerified(false);
			newUser.setEmailIdUuid(UUID.randomUUID().toString());
			newUser.setMobileNo(userDto.getMobileNo());
			newUser.setMobileNoOtp("");
			newUser.setMobileNoVerified(false);
			newUser.setRegistrationDate(new Date());
			newUser.setPassword(userDto.getPassword());

			newUser = userRepo.save(newUser);

			String url = "http://localhost:8080/auth/verifyEmail/" + newUser.getEmailIdUuid();
			boolean sendMail = mailService.sendMail(newUser.getEmailId(),
					"Please click on the below link to verify your email id \n " + url, "Please verify your email id");
			if (sendMail) {
				logger.debug("Mail sent...");
			} else {
				logger.warn("Mail not sent...");
			}

			Phone findByImei = mobileRepo.findByImei(userDto.getDeviceDetails().getImei());
			if (findByImei == null) {

				MobileDTO mobileDTO = userDto.getDeviceDetails();
				Phone phone = new Phone();
				phone.setAppVersion(mobileDTO.getAppVersion());
				phone.setBrand(mobileDTO.getBrand());
				phone.setImei(mobileDTO.getImei());
				phone.setManufacturer(mobileDTO.getManufacturer());
				phone.setModel(mobileDTO.getModel());
				phone.setOsVersion(mobileDTO.getOsVersion());
				phone.setLastAccess(new Date());
				phone.setUserId(newUser.getId());
				mobileRepo.save(phone);
			}
		}
		return newUser;
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
