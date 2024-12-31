package com.coderank.apigateway.ApiGateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/api/users/register",
            "/api/users/authenticate",
            "/api/users/validate",
            "/eureka"
    );


    public Predicate<ServerHttpRequest> isSecured = request -> {
        String path = request.getURI().getPath();
        return openApiEndpoints.stream().noneMatch(path::equalsIgnoreCase);
    };
}
