package com.coderank.authservice.AuthService.model;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    private String email;
    private String password;
}