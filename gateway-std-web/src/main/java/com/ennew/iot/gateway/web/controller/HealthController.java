package com.ennew.iot.gateway.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
