package com.abcapps.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:aws.properties")
@ConfigurationProperties(prefix = "aws")
@Setter
@Getter
public class AwsProperties {

    String accessId;
    String secret;
    String serverKey;

}
