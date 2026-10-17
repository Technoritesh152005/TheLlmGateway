package com.llmgateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//this is thr entry point of application
@SpringBootApplication
@ConfigurationPropertiesScan
public class LlmGatewayApplication {
    public static void main(String[] args) {

        SpringApplication.run(LlmGatewayApplication.class, args);
    }
}
