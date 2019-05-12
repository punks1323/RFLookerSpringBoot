package com.abcapps.service.impl;

import com.abcapps.entity.AwsInfo;
import com.abcapps.entity.User;
import com.abcapps.properties.AwsProperties;
import com.abcapps.repo.UserRepository;
import com.abcapps.service.AwsService;
import com.abcapps.service.DownloadManagerService;
import com.abcapps.utils.AppLogger;
import com.abcapps.utils.AuthUtils;
import com.abcapps.utils.Constants;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AwsServiceImpl implements AwsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AmazonSNS amazonSNS;

    @Autowired
    DownloadManagerService downloadManagerService;

    @Autowired
    AwsProperties awsProperties;

    @Override
    public void createPlatformApplications() {

        Set<String> applicationSet = Arrays.stream(Constants.AWSConstants.APPLICATIONS).collect(Collectors.toSet());
        Set<String> awsAppNames = amazonSNS.listPlatformApplications().getPlatformApplications().stream().map(platformApplication -> platformApplication.getPlatformApplicationArn().split("/")[2]).collect(Collectors.toSet());
        Set<String> appToCreate = applicationSet.stream().filter(app -> !awsAppNames.contains(app)).collect(Collectors.toSet());

        AppLogger.i("New platform applications to create :: " + appToCreate);

        appToCreate.forEach(appName -> {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("PlatformCredential", awsProperties.getServerKey());
            amazonSNS.createPlatformApplication(new CreatePlatformApplicationRequest().withAttributes(attributes).withPlatform("GCM").withName(appName));
            AppLogger.i("Platform application created :: " + appName);
        });
    }

    @Override
    public void createTopics() {
        try {
            Set<String> topicSet = Arrays.stream(Constants.AWSConstants.TOPICS).collect(Collectors.toSet());
            Set<String> awsTopicSet = amazonSNS.listTopics().getTopics().stream().map(topic -> topic.getTopicArn().split(":")[5]).collect(Collectors.toSet());
            Set<String> needToCreateTopicSet = topicSet.stream().filter(t -> !awsTopicSet.contains(t)).collect(Collectors.toSet());

            AppLogger.i("New topics to create :: " + needToCreateTopicSet);

            needToCreateTopicSet.forEach(topicName -> {
                CreateTopicRequest createTopicRequest = new CreateTopicRequest(topicName);
                CreateTopicResult createTopicResult = amazonSNS.createTopic(createTopicRequest);
                AppLogger.i("createTopicRequest :: " + createTopicRequest.toString());
                AppLogger.i("createTopicResult :: " + createTopicResult.toString());
                AppLogger.i("CreateTopicRequest - cached response metadata :: " + amazonSNS.getCachedResponseMetadata(createTopicRequest));
                AppLogger.i(topicName + " : is created");
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEndpoint(String token) {

        String emailId = AuthUtils.getLoggedInUserEmailId();
        User user = userRepository.findByEmailId(emailId);

        AwsInfo awsInfo = user.getAwsInfo();
        if (awsInfo == null) {
            AppLogger.i("New aws info for user : " + emailId);
            awsInfo = new AwsInfo();
            user.setAwsInfo(awsInfo);
        }

        boolean updateNeeded = awsInfo.getDeviceToken() == null || !awsInfo.getDeviceToken().equals(token);

        if (updateNeeded) {
            AppLogger.i("saving new/update token for user :: " + emailId);
            awsInfo.setDeviceToken(token);

            PlatformApplication platformApplication = amazonSNS.listPlatformApplications().getPlatformApplications()
                    .stream()
                    .filter(arn -> arn.getPlatformApplicationArn().split("/")[2].equals(Constants.AWSConstants.APPLICATIONS[0]))
                    .findAny()
                    .orElse(null);

            if (platformApplication == null) {
                AppLogger.e("platformApplication is null");
                return;
            }
            CreatePlatformEndpointRequest cpeq = new CreatePlatformEndpointRequest();
            cpeq.setCustomUserData(user.getEmailId());
            cpeq.setPlatformApplicationArn(platformApplication.getPlatformApplicationArn());
            cpeq.setToken(token);
            CreatePlatformEndpointResult platformEndpoint = amazonSNS.createPlatformEndpoint(cpeq);

            user.getAwsInfo().setEndPointArn(platformEndpoint.getEndpointArn());

            User savedUser = userRepository.save(user);
            Topic topic = amazonSNS.listTopics().getTopics().stream().filter(topic1 -> topic1.getTopicArn().split(":")[5].equals(Constants.AWSConstants.TOPICS[0])).findAny().orElse(null);

            subscribe(topic.getTopicArn(), savedUser.getAwsInfo().getEndPointArn());
            AppLogger.i("Endpoint updated for user :: " + emailId);
        } else {
            AppLogger.i("Token update is not required.");
        }
    }

    @Override
    public void pullFileFromDevice(String filePath) {
        String emailId = AuthUtils.getLoggedInUserEmailId();
        User user = userRepository.findByEmailId(emailId);
        String endpoint = user.getAwsInfo().getEndPointArn();
        PublishRequest ps = new PublishRequest();
        ps.withTargetArn(endpoint);
        ps.setMessage(filePath);
        downloadManagerService.saveNewFileUploadRequest(user, filePath);
        amazonSNS.publish(ps);
    }

    public void subscribe(String topicArn, String endPoint) {
        SubscribeRequest subscribe = new SubscribeRequest(topicArn, "application", endPoint);
        SubscribeResult subscribeResult = amazonSNS.subscribe(subscribe);
        System.out.println("Subscribe request: " + amazonSNS.getCachedResponseMetadata(subscribe));
        System.out.println("Subscribe result: " + subscribeResult);
    }
}
