package com.abcapps.dto;


import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class User {
	@NotNull(message="First name cannot be missing or empty")
	Integer id;
	String name;
	String email;
}
