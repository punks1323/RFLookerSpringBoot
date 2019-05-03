package com.abcapps.entity;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "mobile")
public class Mobile {

	@Id
	@Column(unique = true, nullable = false)
	@NotBlank(message = "1009")
	String imei;

	@NotBlank(message = "1010")
	String manufacturer;

	@NotBlank(message = "1011")
	String brand;

	@NotBlank(message = "1012")
	String model;

	@Column(nullable = false)
	@NotBlank(message = "1013")
	String osVersion;

	@Column(nullable = false)
	@NotBlank(message = "1014")
	String appVersion;

	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	Date lastAccess;

	@OneToOne(mappedBy = "mobile")
	User user;

}
