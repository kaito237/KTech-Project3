package com.example.KTech_Project3.client;


import com.example.KTech_Project3.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class BlockingClient {
    public void testGet() {
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
        UserEntity response = restClient
                .get()
                .uri("/register/1")
                .retrieve()
                .body(UserEntity.class);
                log.info(response.toString());
    }
}
