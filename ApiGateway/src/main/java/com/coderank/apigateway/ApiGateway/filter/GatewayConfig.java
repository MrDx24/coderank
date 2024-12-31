package com.coderank.apigateway.ApiGateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private AuthenticationFilter authFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {

        return builder.routes()
                // TASKQUEUE-SERVICE
                .route("TASKQUEUE-SERVICE", r -> r.path("api/v1/tasks/**")
                .filters(f -> f.filter(authFilter))
                        //.uri("lb://TASKQUEUE-SERVICE"))
                        .uri("http://localhost:8083"))
                // EXECUTION-SERVICE
                .route("EXECUTION-SERVICE", r -> r.path("api/v1/code-execution/**")
                        .filters(f -> f.filter(authFilter))
                        // .uri("lb://EXECUTION-SERVICE"))
                        .uri("http://localhost:8084"))
                .build();

    }

}
