package com.abcapps.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:user.properties")
@ConfigurationProperties(prefix = "user")
@Setter
@Getter
public class UserProperties {
    String secureDataDir;
    String uploadDir;
    String fileTree;
    String fileDeviceDetails;
}

