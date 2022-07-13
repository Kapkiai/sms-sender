package com.kapkiai.smpp.controllers;

import com.kapkiai.smpp.config.Payload;
import com.kapkiai.smpp.config.Response;
import com.kapkiai.smpp.services.SendSms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@Controller
@RestController
@RequestMapping("/api/v1")
public class SendSmsController {

    @Autowired
    SendSms sendMessage;

    @PostMapping("/send")
    public ResponseEntity<Response> sendSms(@RequestBody Payload payload) {
        String response;
        log.debug("Payload {}", payload);
        try {
            response = sendMessage.sendAndWait(payload.message, payload.number, payload.getSenderLabel());
//            return new Response(HttpStatus.OK.getReasonPhrase(), response);
            return ResponseEntity.ok().body(new Response(HttpStatus.OK.getReasonPhrase(), response));
        } catch (Exception e){
            log.error("Failed to send message due to {}",e.getMessage(), e);
//            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "ERROR Sending Message");
            return ResponseEntity.internalServerError().body(new Response(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage()));
        }
    }
}
