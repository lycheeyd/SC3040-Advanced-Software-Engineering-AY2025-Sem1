package com.controller;

import org.springframework.web.client.RestTemplate;

public abstract class HttpReqController {
    
    protected final RestTemplate restTemplate;

    protected HttpReqController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
