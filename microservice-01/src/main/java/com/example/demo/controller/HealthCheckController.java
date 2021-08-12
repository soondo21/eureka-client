package com.example.demo.controller;

import java.util.concurrent.TimeoutException;

import com.example.demo.service.HealthCheckService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
// @RequestMapping("/microservice/client")
public class HealthCheckController {

    Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

    @Autowired
    HealthCheckService healthCheckService;

    @GetMapping("/ping")
    public ResponseEntity<String> healthCheck() {
        logger.info("###health check");
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/host/{param}")
    public ResponseEntity<String> hostTest(@PathVariable(value="param") String param, @RequestHeader HttpHeaders headers) {
        String response = "[MICROSERVICE-01" + headers.get("host") + String.valueOf(param) + "(" + param + ") => working";
        logger.info("### hostTest Called, response : " + response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/circuit/{param}")
    public ResponseEntity<String> circuitTest(@PathVariable(value="param") String param, @RequestHeader HttpHeaders headers) throws Exception{
        String response = "[MICROSERVICE-01, host : " + headers.get("host") + ", param : " + param + " => working";
        logger.info("### circuitTest Called, response : " + response);

        if("fail".equals(param)) {
            try {
                // Thread.sleep(15000);
            } catch(Exception e) {
                e.printStackTrace();
            }
            throw new TimeoutException();
        } else {
            try {
                Thread.sleep(5000);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test/webclient/{param}")
    public Mono<String> msTest(@RequestHeader HttpHeaders headers, @PathVariable(value="param") String param) {

        return healthCheckService.remoteCall(headers, param);
    }

    // @GetMapping("/test/restTemplate")
    // public ResponseEntity<String> restTemplateTest(@RequestHeader HttpHeaders headers) {
    //     // RestTemplate restTemplate = new RestTemplate();
    //     // ResponseEntity<String> response=null;
    //     // try{
    //     //     response=restTemplate.exchange(baseUrl,
    //     //             HttpMethod.GET, getHeaders(),String.class);
    //     // }catch (Exception ex)
    //     // {
    //     //     System.out.println(ex);
    //     // }
    //     // System.out.println(response.getBody());
    //     return healthCheckService.remoteCall2(headers);
    // }    
}