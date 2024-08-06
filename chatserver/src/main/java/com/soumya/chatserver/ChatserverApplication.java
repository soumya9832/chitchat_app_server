package com.soumya.chatserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ChatserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatserverApplication.class, args);
	}

}
