package com.kapkiai.smpp.services;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import com.kapkiai.smpp.utils.Concatenation;
import com.kapkiai.smpp.utils.Gsm0338;
import com.kapkiai.smpp.utils.Ucs2;
import lombok.extern.slf4j.Slf4j;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GSMSpecificFeature;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.MessageMode;
import org.jsmpp.bean.MessageType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.OptionalParameters;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.SMPPSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.kapkiai.smpp.utils.SmppUtils.getTotalSegmentsForTextMessage;
import static com.kapkiai.smpp.utils.SmppUtils.splitIntoStringArray;

@Service
@Slf4j
public class SendSms {

    private static final Random RANDOM = new Random();

    private static final Charset UCS2_CHARSET = StandardCharsets.UTF_16BE;
    private static final Charset ISO_LATIN_CHARSET = StandardCharsets.ISO_8859_1;

    private static final int MAX_SINGLE_MSG_CHAR_SIZE_7BIT = 160;
    private static final int MAX_SINGLE_MSG_CHAR_SIZE_UCS2 = 70;

    @Autowired
//    @Qualifier("smscConnection")
    SmppSessionBean smppSessionBean;

    public String sendAndWait(final String smsMessage, final String number, final String senderLabel) throws IOException {

        // Use messageClass null to set a default message class
        MessageClass messageClass = MessageClass.CLASS1;
        boolean use16bitReference = false;
        // messageBody = "ሃይ አዊ ሰላም ነው አንደት ነው ኣመሰግናለው ደና አደሩ ለምን ታስጨንኩናላቹ ጫት ያመረኪናል\nሰላም ነው አንደት ነው ኣመሰግናለው ደና አደሩ ለምን ታስጨንኩናላቹ ጫት ያመረኪናል\nሰላም ነው አንደት ነው ኣመሰግናለው ደና አደሩ ለምን ታስጨንኩናላቹ ጫት ያመረኪናል\nሰላም ነው አንደት ነው ኣመሰግናለው ደና አደሩ ለምን ታስጨንኩናላቹ ጫት ያመረኪናል\nህጅስድሳድ ልጅሳልድሳ ድክሳጅፖ\nኢአኢውቀ፣ምኅ።ምኅልጅስ ኣኅክሳድልጅ\nሳድልጅሳ፣ድንሳድሳጅልድክጅሳልክድ\nጅሳልድጅልሳክች ኅክችልሳንድላል\nሰላም ነው አንደት ነው ኣመሰግናለው ደና አደሩ ለምን ታስጨንኩናላቹ ጫት ያመረኪናል\nሰላም ነው አንደት ነው ኣመሰግናለው ደና አደሩ ለምን ታስጨንኩናላቹ ጫት ያመረኪናል\nሰላም ነው አንደት ነው ኣመሰግናለው ደና አደሩ ለምን ታስጨንኩናላቹ ጫት ያመረኪናል\nሰላም ነው አንደት ነው ኣመሰግናለው ደና አደሩ ለምን ታስጨንኩናላቹ ጫት ያመረኪናል\nሰላም ነው አንደት ነው ኣመሰግናለው ደና አደሩ ለምን ታስጨንኩናላቹ ጫ";
        String MessageId;
        // When using default alphabet, encoding is determined by the SMSC
        Charset smscDefaultCharset = ISO_LATIN_CHARSET;

        byte[][] messages=null;
        DataCoding dataCoding=null;
        ESMClass esmClass=null;
        StringBuilder GlbMsgId= new StringBuilder();


        if (Gsm0338.isBasicEncodeable(smsMessage)) {
            // Use the SMSC default alphabet
            dataCoding = new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, messageClass);
            // In the GSM network it's send as GSM-7 bit septets packed. See if the characters fit...
            // (could also use messageBody.getBytes(GSM_CHARSET).length)
            if (Gsm0338.countSeptets(smsMessage) > MAX_SINGLE_MSG_CHAR_SIZE_7BIT) {
                // No additional user data header expected, so with 8-bit reference concatenation leaves room for up to 153 septets.
                messages = Concatenation.splitGsm7bit(smsMessage, smscDefaultCharset, RANDOM.nextInt(), use16bitReference);
                esmClass = new ESMClass(MessageMode.DEFAULT, MessageType.DEFAULT, GSMSpecificFeature.UDHI);
            } else {
                messages = new byte[][]{ smsMessage.getBytes(smscDefaultCharset) };
                // set UDHI, as concatenation will add UDH
                esmClass = new ESMClass(MessageMode.DEFAULT, MessageType.DEFAULT, GSMSpecificFeature.DEFAULT);
            }
        } else if (Ucs2.isUcs2Encodable(smsMessage)) {
            dataCoding = new GeneralDataCoding(Alphabet.ALPHA_UCS2, messageClass);
            if (smsMessage.length() > MAX_SINGLE_MSG_CHAR_SIZE_UCS2) {
                // split message according to the maximum available length of a segment, character boundaries, etc.
                messages = Concatenation.splitUcs2(smsMessage, RANDOM.nextInt(), use16bitReference);
                // set UDHI, as concatenation will add UDH
                esmClass = new ESMClass(MessageMode.DEFAULT, MessageType.DEFAULT, GSMSpecificFeature.UDHI);
            } else {
                messages = new byte[][]{ smsMessage.getBytes(UCS2_CHARSET) };
                esmClass = new ESMClass(MessageMode.DEFAULT, MessageType.DEFAULT, GSMSpecificFeature.DEFAULT);
            }
        } else {
            log.error("The message '{}' contains non-encode-able characters", smsMessage);
        }
        // submit all messages
        if (messages != null) {
            for (byte[] message : messages) {
                MessageId = submitMessage(smppSessionBean.getSession(), message, senderLabel, number,
                        dataCoding, esmClass);
                GlbMsgId.append(MessageId).append(",");
                //  log.info("Message submitted, message_id is {}", MessageId);
            }
        }
//        session.unbindAndClose();
        return GlbMsgId.toString();
    }

    public String[] submitLongSMS(String MSISDN, String senderAddr, String message) {

        String[] msgId;
        int splitSize = 135;
        int totalSize = 140;
        int totalSegments = 0;

        new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT);

        GeneralDataCoding dataCoding = new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false);
        ESMClass esmClass = new ESMClass();

        if (message != null && message.length() > totalSize)
        {
            totalSegments = getTotalSegmentsForTextMessage(message);
        }
        Random random = new Random();
        OptionalParameter sarMsgRefNum = OptionalParameters.newSarMsgRefNum((short) random.nextInt());
        OptionalParameter sarTotalSegments = OptionalParameters.newSarTotalSegments(totalSegments);

        String[] segmentData = splitIntoStringArray(message, splitSize, totalSegments);

        msgId = new String[totalSegments];
        for (int i = 0, seqNum; i < totalSegments; i++)
        {
            seqNum = i + 1;
            OptionalParameter sarSegmentSeqnum = OptionalParameters.newSarSegmentSeqnum(seqNum);
            try
            {
                msgId[i] =  smppSessionBean.getSession().submitShortMessage("CMT", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN,
                        "MelroseLabs", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, MSISDN, esmClass,
                        (byte) 0, (byte) 1, null, null, new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE),
                        (byte) 0, dataCoding, (byte) 0, segmentData[i].getBytes(), sarMsgRefNum, sarSegmentSeqnum, sarTotalSegments);

                msgId[i] = smppSessionBean.getSession().submitShortMessage("", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, "MelroseLabs", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, "27827704123", new ESMClass(), (byte)0, (byte)1, null, null, new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT), (byte)0, new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false), (byte)0,
                        segmentData[i].getBytes(), sarMsgRefNum, sarSegmentSeqnum, sarTotalSegments);
                System.out.println("Message id  for segment " + seqNum + " out of totalsegment "
                        + totalSegments + "is" + msgId[i]);
            }
            catch (PDUException e)
            {
                log.error("PDUException has occurred {}", e.getMessage());
            }
            catch (ResponseTimeoutException e)
            {
                 log.error("ResponseTimeoutException has occurred {}", e.getMessage());
            }
            catch (InvalidResponseException e)
            {
                 log.error("InvalidResponseException has occurred {}", e.getMessage());
            }
            catch (NegativeResponseException e)
            {
                 log.error("NegativeResponseException has occurred {}", e.getMessage());
            }
            catch (IOException e)
            {
                 log.error("IOException has occurred {}", e.getMessage());
            }
        }
        return msgId;
    }

    private String submitMessage(SMPPSession session, byte[] message, String sourceMsisdn, String destinationMsisdn,
                                 DataCoding dataCoding, ESMClass esmClass) {

        String result = "";

        try {
            result = session.submitShortMessage("CMT", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN,
                    sourceMsisdn, TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, destinationMsisdn, esmClass,
                    (byte) 0, (byte) 1, null, null, new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE),
                    (byte) 0, dataCoding, (byte) 0, message);
        } catch (PDUException e) {
             log.error("Invalid PDU parameter", e);
        } catch (ResponseTimeoutException e) {
            log.error("Response timeout", e);
        } catch (InvalidResponseException e) {
             log.error("Receive invalid response", e);
        } catch (NegativeResponseException e) {
             log.error("Receive negative response", e);
        } catch (IOException e) {
             log.error("I/O error occurred", e);
        }
        return result;
    }

}
