package com.abcapps.service;

public interface AwsService {

    void createTopicIfNotExists(String topic);

    void subscribe(String topic, String deviceId);
}
