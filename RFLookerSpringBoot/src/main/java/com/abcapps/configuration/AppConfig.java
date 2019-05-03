package com.abcapps.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/*@EnableWebMvc
@Configuration*/
@ComponentScan("com.abcapps")
@Configuration
//@PropertySources({@PropertySource("aws.properties")})
public class AppConfig {

}
