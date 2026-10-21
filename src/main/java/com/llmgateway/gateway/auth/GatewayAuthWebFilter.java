package com.llmgateway.gateway.auth;

import jakarta.servlet.annotation.WebFilter;
import jakarta.websocket.server.PathParam;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;

//it is a class or web fikter is interface which takes http request and response and validates whether it can be send to our deeper version api controller

@Component
public class GatewayAuthWebFilter implements WebFilter {

    //any request or response comes it musy say r check this endpoint it is going so it needs authentication
    //means request going to this path needs authentication
    private static final PathPattern DATA_PLANE = PathPattern.defaultInstance.parse("/v1/**");
    private static final String Bearer = "Bearer";

    private final ApiKeyAuthenticator authenticator;
}
