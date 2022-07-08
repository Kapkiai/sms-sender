package com.kapkiai.smpp.controllers;

import com.kapkiai.smpp.config.Payload;
import com.kapkiai.smpp.config.Response;
import com.kapkiai.smpp.services.SendSms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@Controller
@RestController
@RequestMapping("/api/v1")
public class SendSmsController {

    @Autowired
    SendSms sendMessage;

    @PostMapping("/send")
    public Response sendSms(@RequestBody Payload payload) throws IOException {
        String response;
        try {
            response = sendMessage.sendAndWait(payload.message, payload.number, payload.getSenderLabel());
            return new Response(HttpStatus.OK.getReasonPhrase(), response);
        } catch (Exception e){
            return new Response(HttpStatus.BAD_GATEWAY.getReasonPhrase(), "ERROR Sending Message");
        }
    }
}
