package com.abcapps.controllers;

import com.abcapps.entity.AwsInfo;
import com.abcapps.entity.User;
import com.abcapps.repo.UserRepository;
import com.abcapps.service.AwsService;
import com.abcapps.utils.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;

@RestController
@RequestMapping("/aws")
public class AwsController {

    @Autowired
    AwsService awsService;

    @PostMapping("/sendToken")
    private ResponseEntity saveMobileDeviceToken(@RequestParam("token") String token) {
        awsService.updateEndpoint(token);
        return new ResponseEntity(HttpStatus.OK);
    }
}
