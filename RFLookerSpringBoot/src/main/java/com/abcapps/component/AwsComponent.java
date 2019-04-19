package com.abcapps.component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AwsComponent {


    @Value("${rflooker.aws.access_id}")
    private String accessId;
    @Value("${rflooker.aws.secret}")
    private String secretKey;

    @Bean
    public AmazonSNS getAmazonSNSClient() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessId, secretKey);

        return AmazonSNSClient.builder().withRegion(Regions.AP_SOUTH_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

    }

}
