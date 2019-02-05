package com.abcapps.Entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

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
	@Null(message = "1001")
	Long id;
	
	@NotBlank(message = "1002")
	String name;

	@NotBlank(message = "1003")
	@Size(min = 10, max = 10)
	String mobileNo;

	@Null(message = "1001")
	String mobileNoOtp;

	@Null(message = "1001")
	Boolean mobileNoVerified;

	@Column(unique = true)
	@NotBlank(message = "1004")
	@Email(message = "1005")
	String emailId;

	@Null(message = "1001")
	String emailIdUuid;

	@Null(message = "1001")
	Boolean emailIdVerified;

	@NotBlank(message = "1006")
	@Size(min = 6)
	String password;

	@NotBlank(message = "1007")
	@Size(min = 6)
	@Transient
	String confirmPassword;

	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	Date registrationDate;

	@NotNull
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "mobile_imei")
	Mobile mobile;
}
