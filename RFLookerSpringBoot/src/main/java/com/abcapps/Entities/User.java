package com.abcapps.Entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	String name;

	String mobileNo;

	String mobileNoOtp;

	Boolean mobileNoVerified;

	@Column(unique = true)
	String emailId;

	String emailIdUuid;

	Boolean emailIdVerified;

	String password;

	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	Date registrationDate;
}
