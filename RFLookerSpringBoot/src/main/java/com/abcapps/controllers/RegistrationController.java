package com.abcapps.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abcapps.entity.User;
import com.abcapps.exception.EmailIdAlreadyExists;
import com.abcapps.exception.MobileNoAlreadyExists;
import com.abcapps.exception.PasswordNotMatchException;
import com.abcapps.service.UserService;

@RestController
@RequestMapping("/auth")
public class RegistrationController {

	Logger log = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	UserService userService;

	@PostMapping(value = "/register")
	public ResponseEntity<Object> register(@Valid @RequestBody User user) {
		try {
			return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
		} catch (EmailIdAlreadyExists e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (PasswordNotMatchException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (MobileNoAlreadyExists e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "/verifyEmail/{uuid}")
	public ResponseEntity<Object> verifyEmail(@PathVariable String uuid) {
		log.info("Email verification :: " + uuid);
		try {
			return new ResponseEntity<>(
					userService.verifyEmailByEmailUUID(uuid) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
