package com.abcapps.dto;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class UserDTO {
	@Null(message = "1001")
	Long id;

	@NotBlank(message = "1002")
	String name;

	@NotBlank(message = "1003")
	@Size(min = 10, max = 10)
	String mobileNo;

	@Null(message = "1001")
	String mobileNoOTP;

	@Null(message = "1001")
	Boolean mobileNoVerified;

	@NotBlank(message = "1004")
	@Email(message = "1005")
	String emailId;

	@Null(message = "1001")
	String emailIdUUID;

	@Null(message = "1001")
	Boolean emailIdVerified;

	@NotBlank(message = "1006")
	@Size(min = 6)
	String password;

	@NotBlank(message = "1007")
	@Size(min = 6)
	String confirmPassword;

	@NotNull(message = "1008")
	@Valid
	MobileDTO deviceDetails;

}
