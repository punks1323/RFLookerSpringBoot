package com.abcapps.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    @Null(message = "1001")
    Long id;

    @NotBlank(message = "1002")
    String name;

    @NotBlank(message = "1003")
    @Size(min = 10, max = 10)
    String mobileNo;

    @Column(unique = true)
    @JsonIgnore
    @Null(message = "1001")
    String mobileNoOtp;

    @JsonIgnore
    @Null(message = "1001")
    Boolean mobileNoVerified;

    @Column(unique = true)
    @NotBlank(message = "1004")
    @Email(message = "1005")
    String emailId;

    @JsonIgnore
    @Null(message = "1001")
    String emailIdUuid;

    @JsonIgnore
    @Null(message = "1001")
    Boolean emailIdVerified;

    @JsonProperty(access = Access.WRITE_ONLY)
    @NotBlank(message = "1006")
    @Size(min = 6)
    String password;

    @JsonProperty(access = Access.WRITE_ONLY)
    @NotBlank(message = "1007")
    @Size(min = 6)
    @Transient
    String confirmPassword;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    Date registrationDate;

    @JsonIgnore
    Boolean isActive;

    @JsonProperty(access = Access.WRITE_ONLY)
    @NotNull
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "mobile_imei")
    Mobile mobile;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private SDCard sdcard;

}
