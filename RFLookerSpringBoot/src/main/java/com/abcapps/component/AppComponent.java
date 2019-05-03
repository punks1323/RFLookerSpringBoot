package com.abcapps.component;


import com.abcapps.service.AwsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AppComponent {

    @Autowired
    AwsService awsService;

    @PostConstruct
    private void afterStart() {
        awsService.createPlatformApplications();
        awsService.createTopics();
    }
}
