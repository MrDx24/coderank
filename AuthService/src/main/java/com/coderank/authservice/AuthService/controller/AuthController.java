package com.coderank.authservice.AuthService.controller;

import com.coderank.authservice.AuthService.entity.Users;
import com.coderank.authservice.AuthService.model.LoginDto;
import com.coderank.authservice.AuthService.repository.AuthRepository;
import com.coderank.authservice.AuthService.service.AuthService;
import com.coderank.authservice.AuthService.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
                          AuthService authService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Users userDetails) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (authService.emailExists(userDetails.getEmail())) {
                response.put("status", "Error");
                response.put("message", "Email already exists");
                return ResponseEntity.ok(response);
            }

            userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            Users userResp = authService.saveUser(userDetails);

            response.put("status", "Success");
            response.put("message", "User Registered Successfully");
            response.put("userName", userResp.getUsername()); // example of additional data if needed
            response.put("userEmail", userResp.getEmail()); // example of additional data if needed
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "Error");
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDto loginDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                String email = authentication.getName();

                Users userDetails = authService.findUserByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

                String token = jwtService.generateToken(userDetails.getEmail());
                response.put("status", "Success");
                response.put("message", "Login successful");
                response.put("token", token);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "Error");
                response.put("message", "Invalid Email or Password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("status", "Error");
            response.put("message", "Invalid Email");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authToken) {
        Map<String, Object> response = new HashMap<>();

        String token = authToken;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            boolean checkClaim = jwtService.isTokenValid(token);

            if (checkClaim) {
                response.put("status", "Success");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "Authentication failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("status", "Error");
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test")
    public String hello() {
        return "Hello, form user service";
    }

}
