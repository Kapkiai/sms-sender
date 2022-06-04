package com.kapkiai.smpp.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties
@Configuration
@Data
public class SmppConfig {

    @Value("${smppHost:localhost}")
    private String host;
    @Value("${smppPort:2775}")
    private String port;
    @Value("${smppSystemId:smsc}")
    private String systemId;
    @Value("${smppPassword:smsc}")
    private String password;

}
