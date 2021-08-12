package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/microservice/server")
public class HealthCheckController {

    @Value("${server.port}")
    private int port;

    Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

    @GetMapping("/ping2")
    public ResponseEntity<String> healthCheck() {
        logger.info("###health check");
        return ResponseEntity.ok("pong2");
    }

    @GetMapping("/test/{param}")
    public String msTest(@PathVariable(value="param") String param, @RequestHeader HttpHeaders headers) {
        for(String key :headers.keySet()) {
            logger.info("[MICROSERVICE-02-1] KEY : " + key + ", VALUE : " + headers.get(key));
        }
        String response = "[MICROSERVICE-02-1]" + headers.get("host") + String.valueOf(param) + "(" + param + ") => working";
        logger.info("### msTest Called, response : " + response);
        return response;
    }
}