package com.kapkiai.smpp.services;

import java.nio.charset.StandardCharsets;

import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.Session;
import org.jsmpp.util.InvalidDeliveryReceiptException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageReceiverListenerImpl implements MessageReceiverListener {

    public void onAcceptDeliverSm(final DeliverSm deliverSm)
            throws ProcessRequestException {

        if (deliverSm.isSmscDeliveryReceipt()) {

            try {
                final DeliveryReceipt delReceipt = deliverSm.getShortMessageAsDeliveryReceipt();

                log.info("Received receipt: {} {}", delReceipt.getId(), delReceipt.getFinalStatus());

                final String messageId = delReceipt.getId();
                log.info("Message ID {}", messageId);

            } catch (InvalidDeliveryReceiptException e) {
                log.error("Failed getting delivery receipt", e);
            }
        } else {
            log.info("Message Received {}", new String(deliverSm.getShortMessage(), StandardCharsets.ISO_8859_1));
        }
    }

    public void onAcceptAlertNotification(AlertNotification alertNotification) {
        log.info("Receiving alert notification from {}: {}", alertNotification.getSourceAddr(), alertNotification.getEsmeAddr());
    }

    public DataSmResult onAcceptDataSm(DataSm dataSm, Session source)
            throws ProcessRequestException {
        log.info("Receiving data sm from {} to {}", dataSm.getSourceAddr(), dataSm.getDestAddress());
        throw new ProcessRequestException("data_sm is not implemented", 99);
    }
}

