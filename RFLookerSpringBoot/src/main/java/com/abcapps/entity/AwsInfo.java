package com.abcapps.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Setter
@Getter
@Entity
@Table(name = "aws_info")
public class AwsInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    String deviceToken;
    String endPointArn;

    @OneToOne(mappedBy = "awsInfo")
    User user;
}
