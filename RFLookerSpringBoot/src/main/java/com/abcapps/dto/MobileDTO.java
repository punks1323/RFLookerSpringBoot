package com.abcapps.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class MobileDTO {

	@NotBlank(message = "1009")
	String imei;

	@NotBlank(message = "1010")
	String manufacturer;

	@NotBlank(message = "1011")
	String brand;

	@NotBlank(message = "1012")
	String model;

	@NotBlank(message = "1013")
	String osVersion;

	@NotBlank(message = "1014")
	String appVersion;
}
