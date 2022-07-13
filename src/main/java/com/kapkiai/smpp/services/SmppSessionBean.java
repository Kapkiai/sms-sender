package com.kapkiai.smpp.services;

import com.kapkiai.smpp.config.SmppConfig;
import com.kapkiai.smpp.pool.PooledSMPPSession;
import com.kapkiai.smpp.pool.ThrottledSMPPSession;
import org.jsmpp.session.SMPPSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmppSessionBean {

    @Autowired
    SmppConfig smppConfig;

    @Bean(name = "smscConnection")
    public PooledSMPPSession<ThrottledSMPPSession> getSession() throws Exception {
        SmppSession session = new  SmppSession(smppConfig.getSystemId(), smppConfig.getSmppPassword(), smppConfig.getHost(), smppConfig.getPort());
        return session.getSmppSession();
    }
}
