package com.abcapps.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aws")
public class AwsController {
    Logger log = LoggerFactory.getLogger(AwsController.class);


    @PostMapping("/sendToken")
    private void saveMobileDeviceToken(@RequestParam("token") String token) {

    }
}