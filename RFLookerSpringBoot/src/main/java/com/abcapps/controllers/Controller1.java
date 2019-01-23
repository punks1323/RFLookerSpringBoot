package com.abcapps.controllers;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abcapps.dto.User;

@RestController
@RequestMapping("auth")
public class Controller1 {

	@PostMapping(value = "/register")
	public void m1(@Valid @RequestBody User user) {
		System.out.println("Request came:: " + user);
	}
}
