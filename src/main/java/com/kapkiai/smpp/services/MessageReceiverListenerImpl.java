package com.kapkiai.smpp.services;

import java.nio.charset.StandardCharsets;

import org.jsmpp.bean.*;
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

        if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {

            try {
                final DeliveryReceipt delReceipt = deliverSm.getShortMessageAsDeliveryReceipt();

                log.info("Received receipt: {} {}", delReceipt.getId(), delReceipt.getFinalStatus());

                final String messageId = delReceipt.getId();
                log.info("Receipt ID {}", messageId);

                // lets cover the id to hex string format
                long id = Long.parseLong(delReceipt.getId()) & 0xffffffff;
                String mId = Long.toString(id, 16).toUpperCase();

                /*
                 * you can update the status of your submitted message on the
                 * database based on messageId
                 */

                log.info("Receiving delivery receipt for message '{}' from {} to {}: {}",
                        mId, deliverSm.getSourceAddr(), deliverSm.getDestAddress(), delReceipt);

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

