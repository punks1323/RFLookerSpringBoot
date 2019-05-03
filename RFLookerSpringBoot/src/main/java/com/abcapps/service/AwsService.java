package com.abcapps.service;

import com.abcapps.entity.User;

public interface AwsService {

    void createPlatformApplications();

    void createTopics();

    void updateEndpoint(String token);


}
