package com.example.demo.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
 
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        HttpClient client = HttpClientBuilder.create()
            .setMaxConnTotal(50)
            .setMaxConnPerRoute(20)
            .build();
        
            factory.setHttpClient(client);
            factory.setConnectTimeout(2000);
            factory.setReadTimeout(5000);

            return new RestTemplate(factory);
    }
}
