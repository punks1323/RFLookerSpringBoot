package com.abcapps.service;

public interface AwsService {

    void createPlatformApplications();

    void createTopics();

    void updateEndpoint(String token);

    void pullFileFromDevice(String path);

}
