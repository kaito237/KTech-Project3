package com.example.KTech_Project3.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final TemplateClient templateClient;
    private final BlockingClient blockingClient;
    public TestController(
            TemplateClient templateClient,
            BlockingClient blockingClient
    ) {
        this.templateClient = templateClient;
        this.blockingClient = blockingClient;
    }

    @GetMapping("test")
    public String test() {
//        templateClient.testGet();
//        templateClient.testPost();
//        templateClient.testPut();
//        templateClient.testDelete();
        blockingClient.testGet();
        return "success";
    }
}
