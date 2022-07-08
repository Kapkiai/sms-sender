package com.kapkiai.smpp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmsSender {


	public static void main(String[] args) {
		SpringApplication.run(SmsSender.class, args);
	}

}
