package com.example.KTech_Project3.client;

import com.example.KTech_Project3.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class TemplateClient {
    private final RestTemplate restTemplate;
    public TemplateClient(RestTemplateBuilder templateBuilder) {
        restTemplate = templateBuilder
                .rootUri("http://localhost:8081")
                .build();
    }

    public void testGet() {
        RestTemplate restTemplate = new RestTemplate();
        UserEntity response = restTemplate.getForObject(
                "http://localhost:8081/articles/1",
                UserEntity.class
        );
        log.info(response.toString());

        ResponseEntity<UserEntity> responseEntity = this.restTemplate.getForEntity(
                "/articles/1",
                UserEntity.class
        );
        log.info(responseEntity.getStatusCode().toString());
        log.info(responseEntity.getHeaders().toString());
        log.info(responseEntity.getBody().toString());
    }

    public void testPost() {
        UserEntity newUserEntity = new UserEntity();

        newUserEntity.setName("");
        newUserEntity.setEmail("");
        newUserEntity.setPhone("");



        UserEntity response = restTemplate.postForObject(
                "/register",
                newUserEntity,
                UserEntity.class
        );
        log.info(response.toString());
    }

    public void testPut() {
        UserEntity updateUserEntity = new UserEntity();

        updateUserEntity.setName("update");
        updateUserEntity.setEmail("");
        updateUserEntity.setPhone("");

        ResponseEntity<UserEntity> response = restTemplate.exchange(
                "/register/1",
                HttpMethod.PUT,
                new HttpEntity<>(updateUserEntity),
                UserEntity.class
        );
        log.info(response.toString());
    }

    public void testDelete() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/register/10",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        log.info("status code: {}", response.getStatusCode());
    }
}