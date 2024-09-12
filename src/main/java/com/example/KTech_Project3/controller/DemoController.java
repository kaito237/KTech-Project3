package com.example.KTech_Project3.controller;

import com.example.KTech_Project3.client.TemplateClient;
import com.example.KTech_Project3.entity.UserEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@RestController
public class DemoController {
    private final TemplateClient templateClient;

    public DemoController(TemplateClient templateClient) {
        this.templateClient = templateClient;
    }

    @GetMapping("/hello")
    public String hello(){
        return this.getHello();
    }

    private String getHello() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForEntity(
                "http://localhost:8081/api/hello", String.class
        ).getBody();
    }
}
