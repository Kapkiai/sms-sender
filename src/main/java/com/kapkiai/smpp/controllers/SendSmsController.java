package com.kapkiai.smpp.controllers;

import com.kapkiai.smpp.config.Payload;
import com.kapkiai.smpp.config.Response;
import com.kapkiai.smpp.services.SendSms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Controller
@RestController
public class SendSmsController {

    @Autowired
    SendSms sendMessage;

    @PostMapping("/send")
    public Response sendSms(@RequestBody Payload payload) throws IOException {
        String response;
        response = sendMessage.sendAndWait(payload.message, payload.number, payload.getSenderLabel());
        return new Response(response);
    }
}
