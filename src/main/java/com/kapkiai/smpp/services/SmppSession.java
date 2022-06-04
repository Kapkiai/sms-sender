package com.kapkiai.smpp.services;

import lombok.extern.slf4j.Slf4j;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.Session;
import org.jsmpp.session.SessionStateListener;
import org.springframework.context.annotation.Bean;

@Slf4j
public class SmppSession {

    private final String systemId;
    private final String password;
    private final String host;
    private final String port;

    public SmppSession(final String systemId, final String password, final String host, final String port){
        this.systemId = systemId;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    @Bean
    public SMPPSession getSession() {
        log.info("CONNECTION DETAILS: Host= {}, port= {},system id=  {}",host, port, systemId);
        BindParameter bindParam = new BindParameter(BindType.BIND_TRX, systemId, password, "tdd",
                TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null);

        SMPPSession session = new SMPPSession();
        session.addSessionStateListener(new SessionStateListenerImpl());
        session.setMessageReceiverListener(new MessageReceiverListenerImpl());
        try {
            session.connectAndBind(host, Integer.parseInt(port), bindParam);
        } catch (Exception e){
            log.error("Failed to connect and bind to smsc {} due to {}", host, e);
        }
        return session;
    }

    private static class SessionStateListenerImpl implements SessionStateListener {
        public void onStateChange(SessionState newState, SessionState oldState, Session source) {
             log.info("Session {} state changed from {} to {}", source.getSessionId(), oldState, newState);
        }
    }
}
