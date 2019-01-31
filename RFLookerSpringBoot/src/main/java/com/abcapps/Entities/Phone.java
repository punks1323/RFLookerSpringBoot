package com.abcapps.Entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Entity
public class Phone {

	@Id
	@Column(unique = true, nullable = false)
	String imei;

	String manufacturer;

	String brand;

	String model;

	@Column(nullable = false)
	String osVersion;

	@Column(nullable = false)
	String appVersion;

	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	Date lastAccess;

	@Column(nullable = false)
	long userId;

}
