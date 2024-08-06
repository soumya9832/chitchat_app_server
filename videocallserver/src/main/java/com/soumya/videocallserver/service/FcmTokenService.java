package com.soumya.videocallserver.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class FcmTokenService {
    private final WebClient.Builder webClientBuilder;

    public Mono<String> getFcmToken(String username){
        return webClientBuilder.baseUrl("http://chatserver").build()
                .get()
                .uri("/api/{username}/fcm-token",username)
                .retrieve()
                .bodyToMono(String.class);
    }

}
