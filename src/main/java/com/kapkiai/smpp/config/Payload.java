package com.kapkiai.smpp.config;

import lombok.Data;

@Data
public class Payload {

    public String message;
    public String number;
    public String senderLabel;
}
