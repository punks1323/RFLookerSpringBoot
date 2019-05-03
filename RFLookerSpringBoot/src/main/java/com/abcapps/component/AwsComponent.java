package com.abcapps.component;

import com.abcapps.properties.AwsProperties;
import com.abcapps.utils.AppLogger;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AwsComponent {


    @Autowired
    AwsProperties awsProperties;

    @Bean
    public AmazonSNS getAmazonSNSClient() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsProperties.getAccessId(), awsProperties.getSecret());
        return AmazonSNSClient.builder().withRegion(Regions.AP_SOUTH_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

    }

}
