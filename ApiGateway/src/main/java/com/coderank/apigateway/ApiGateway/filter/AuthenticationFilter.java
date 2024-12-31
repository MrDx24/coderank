package com.coderank.apigateway.ApiGateway.filter;

import com.coderank.apigateway.ApiGateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Autowired
    private RouteValidator routeValidator;

    private final WebClient webClient = WebClient.create();

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {

    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String requestPath = exchange.getRequest().getURI().getPath();
            log.info("Incoming Request Path: {}", requestPath);

            if (routeValidator.isSecured.test(exchange.getRequest())) {
                log.info("Secured Route: {}", requestPath);

                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    log.warn("Missing Authorization Header for path: {}", requestPath);
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                return webClient.get()
                        .uri(authServiceUrl + "/validate?token=" + token)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .flatMap(response -> {
                            if (!"Success".equals(response.get("status"))) {
                                log.warn("Invalid Token for path: {}", requestPath);
                                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                return exchange.getResponse().setComplete();
                            }
                            return chain.filter(exchange);
                        })
                        .onErrorResume(e -> {
                            log.error("Token validation failed for path: {}", requestPath, e);
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        });
            } else {
                log.info("Unsecured Route: {}", requestPath);
            }

            return chain.filter(exchange);
        };
    }



}
