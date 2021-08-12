package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import reactor.core.publisher.Mono;

@Service
public class HealthCheckService {

    Logger logger = LoggerFactory.getLogger(HealthCheckService.class);

    private static final String TEST_CIRCUIT_BREAKER = "nhCircuitBreaker";

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WebClient webClient;

    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    HealthCheckService(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        this.lbFunction = lbFunction;
    }

    // @RateLimiter(name = TEST_CIRCUIT_BREAKER)
    // @Bulkhead(name = TEST_CIRCUIT_BREAKER)
    // @CircuitBreaker(name = TEST_CIRCUIT_BREAKER, fallbackMethod = "fallback")
    @CircuitBreaker(name = TEST_CIRCUIT_BREAKER)
    // @Retry(name = TEST_CIRCUIT_BREAKER)
    @TimeLimiter(name = TEST_CIRCUIT_BREAKER)
    public Mono<String> remoteCall(@RequestHeader HttpHeaders headers, String param) {

        for(String key :headers.keySet()) {
            logger.info("[MICROSERVICE-01] KEY : " + key + ", VALUE : " + headers.get(key));
        }

        // WebClient webClient = WebClient.builder()
        // // WebClientConfig webClientConfig = new WebClientConfig();
        // // WebClient webClient = webClientConfig.webClientBuilder()
        //     // .filter(this.lbFunction)
        //     // .baseUrl("http://microservice-02")
        //     .baseUrl("http://localhost:8082")
        //     .build();
        
        logger.info("### microservice-01 Called, webClient : " + webClient);
        return webClient.get()
            .uri("/microservice/server/test/" + param)
            .retrieve()
            .bodyToMono(String.class);
    }

    public ResponseEntity<String> remoteCall2(@RequestHeader HttpHeaders headers) {

        for(String key :headers.keySet()) {
            logger.info("[MICROSERVICE-01] KEY : " + key + ", VALUE : " + headers.get(key));
        }

        // return restTemplate.getForEntity("http://localhost:8082/microservice/server/test/sample02", String.class);
        return restTemplate.getForEntity("http://microservice-02:8082/microservice/server/test/sample02", String.class);
    }

    public Mono<String> fallback(CallNotPermittedException e) {
    return Mono.just("CallNotPermittedException");
    }
}