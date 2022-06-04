package com.kapkiai.smpp.services;

import com.kapkiai.smpp.config.SmppConfig;
import org.jsmpp.session.SMPPSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmppSessionBean {

    @Autowired
    SmppConfig smppConfig;

    @Bean(name = "smscConnection")
    public SMPPSession getSession(){
        SmppSession session = new  SmppSession(smppConfig.getSystemId(), smppConfig.getPassword(), smppConfig.getHost(), smppConfig.getPort());
        return session.getSession();
    }
}
