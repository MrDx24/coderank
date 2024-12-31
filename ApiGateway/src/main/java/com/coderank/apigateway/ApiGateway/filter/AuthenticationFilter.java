package com.coderank.apigateway.ApiGateway.filter;

import com.coderank.apigateway.ApiGateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RefreshScope
@Component
@Slf4j
public class AuthenticationFilter implements GatewayFilter{

    private final RouteValidator validator;
    private final JwtUtil jwtUtil;
    private final WebClient webClient;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Autowired
    public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil, WebClient.Builder webClientBuilder) {
        this.validator = validator;
        this.jwtUtil = jwtUtil;
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        log.info("Incoming Request: {}", request);

        if (validator.isSecured.test(request)) {
            if (authMissing(request)) {
                return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Validate the token by calling the AuthService
            try {
                return webClient.post()
                        .uri(authServiceUrl + "/validate")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .onStatus(
                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                response -> Mono.error(new RuntimeException("Invalid Token or Token Validation Failed"))
                        )
                        .bodyToMono(Map.class)
                        .flatMap(response -> {
                            if (!"Success".equals(response.get("status"))) {
                                log.warn("Token Validation Failed: {}", response);
                                return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
                            }
                            return chain.filter(exchange);
                        });
            } catch (Exception e) {
                log.error("Error during token validation", e);
                return onError(exchange, "Error Validating Token", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return chain.filter(exchange);
    }

    private boolean authMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error("Error: {} | Status Code: {}", errorMessage, httpStatus);

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Create the error response body
        String errorJson = String.format("{\"error\": \"%s\"}", errorMessage);

        // Write the JSON response to the body
        DataBuffer dataBuffer = response.bufferFactory().wrap(errorJson.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer));
    }
}
