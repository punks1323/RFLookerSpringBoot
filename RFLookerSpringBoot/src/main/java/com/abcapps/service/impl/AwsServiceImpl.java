package com.abcapps.service.impl;

import com.abcapps.service.AwsService;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AwsServiceImpl implements AwsService {

    @Autowired
    AmazonSNS amazonSNS;

    @Override
    public void createTopicIfNotExists(String topicName) {
        try {
            //amazonSNS.topic
            ListTopicsResult listTopicsResult = amazonSNS.listTopics();
            listTopicsResult.withTopics();

            // create a new SNS topic
            CreateTopicRequest createTopicRequest = new CreateTopicRequest(topicName);
            CreateTopicResult createTopicResult = amazonSNS.createTopic(createTopicRequest);

            // print TopicArn
            System.out.println(createTopicResult); // get request id for

            // CreateTopicRequest from SNS metadata
            System.out.println("CreateTopicRequest - " + amazonSNS.getCachedResponseMetadata(createTopicRequest));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void subscribe(String topic, String deviceId) {

    }
}
