package com.abcapps.service.impl;

import com.abcapps.service.AwsService;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AwsServiceImpl implements AwsService {

    Logger log = LoggerFactory.getLogger(AwsServiceImpl.class);
    @Autowired
    AmazonSNS amazonSNS;

    @Override
    public void createTopicIfNotExists(String topicName) {
        try {
            boolean isTopicPresent = false;
            for (Topic topic : amazonSNS.listTopics().getTopics()) {
                if (topic.getTopicArn().split(":")[5].equals(topicName)) {
                    isTopicPresent = true;
                    break;
                }
            }

            if (!isTopicPresent) {
                // create a new SNS topic
                CreateTopicRequest createTopicRequest = new CreateTopicRequest(topicName);
                CreateTopicResult createTopicResult = amazonSNS.createTopic(createTopicRequest);

                log.info(createTopicRequest.toString());
                log.info("CreateTopicRequest - " + amazonSNS.getCachedResponseMetadata(createTopicRequest));
            } else {
                log.info(topicName + " : topic is already present.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void subscribe(String topic, String deviceId) {

    }
}
