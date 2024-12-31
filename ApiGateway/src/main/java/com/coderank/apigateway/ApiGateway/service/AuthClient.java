package com.coderank.apigateway.ApiGateway.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "AUTH-SERVICE", path = "/api/v1/users")
public interface AuthClient {
    @PostMapping("/validate")
    ResponseEntity<Map<String, Object>> validateToken(@RequestParam("token") String token);
}
