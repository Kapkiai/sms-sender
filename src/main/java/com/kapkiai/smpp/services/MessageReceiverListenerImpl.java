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

        log.info("onAcceptDeliverSm");

        if (deliverSm.isSmscDeliveryReceipt()) {
            // if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
            // this message is delivery receipt
            try {
                final DeliveryReceipt delReceipt = deliverSm.getShortMessageAsDeliveryReceipt();

                log.info("Received receipt: {} {}", delReceipt.getId(), delReceipt.getFinalStatus());

                // lets cover the id to hex string format
                // long id = Long.parseLong(delReceipt.getId()) & 0xffffffff;
                // String messageId = Long.toString(id, 16).toUpperCase();
                final String messageId = delReceipt.getId();
                log.info("Message ID {}", messageId);

//        /*
//         * you can update the status of your submitted message on the database based on messageId
//         */
//        LOG.info("Pending messageId {}", messageId);
//        final PendingReceipt<Command> c = smsService.removePendingResponse(messageId);
//        if (c != null) {
//          c.done(deliverSm);
//        }
//
//        LOG.info("Receiving delivery receipt on {} for message '{}' from {} to {}: {}",
//            connectionId, messageId, deliverSm.getSourceAddr(), deliverSm.getDestAddress(), delReceipt);
//        applicationEventPublisher.publishEvent(new DeliveryReceiptEvent(this, connectionId, delReceipt, deliverSm));

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

    private byte getOptionalByte(final OptionalParameter optionalParameter) {
        // jSMPP will give as Octet String
        final OptionalParameter.OctetString octetString = (OptionalParameter.OctetString) optionalParameter;
        if (octetString == null) {
            return 0x00;
        }
        final byte[] value = octetString.getValue();
        if (value.length != 1) {
            throw new IllegalArgumentException("The optional parameter " + optionalParameter.tag + " has invalid contents for Byte");
        }
        return value[0];
    }
}

