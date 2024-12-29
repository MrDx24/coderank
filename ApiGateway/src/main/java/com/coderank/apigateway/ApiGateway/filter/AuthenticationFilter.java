package com.coderank.apigateway.ApiGateway.filter;

import com.coderank.apigateway.ApiGateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {

    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange,chain) -> {


            if(routeValidator.isSecured.test(exchange.getRequest())) {
                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    throw new RuntimeException("Missing Auth Headers");
                }
            }

            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if(token!=null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            try {
                jwtUtil.isTokenValid(token);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            System.out.println("check : " + exchange.getRequest().getHeaders());
            System.out.println("check : " + exchange.getResponse().getStatusCode());
            System.out.println("check : " + exchange.getLogPrefix());
            return chain.filter(exchange);
        });
    }



}
