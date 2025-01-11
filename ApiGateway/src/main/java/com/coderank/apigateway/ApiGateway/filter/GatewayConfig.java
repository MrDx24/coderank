package com.coderank.apigateway.ApiGateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {


    private final AuthenticationFilter authFilter;

    @Autowired
    public GatewayConfig(AuthenticationFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {

        return builder.routes()
                // AUTH-SERVICE
                .route("AUTH-SERVICE", r -> r.path("/api/v1/users/**")
                        .uri("lb://AUTH-SERVICE"))
                        //.uri("http://localhost:8082"))
                // TASKQUEUE-SERVICE
                .route("TASKQUEUE-SERVICE", r -> r.path("/api/v1/tasks/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://TASKQUEUE-SERVICE"))
                        //.uri("http://localhost:8083"))
                // EXECUTION-SERVICE
                .route("EXECUTION-SERVICE", r -> r.path("/api/v1/code-execution/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://EXECUTION-SERVICE"))
                        //.uri("http://localhost:8084"))
                .build();

    }

}
