package com.coderank.authservice.AuthService.service;

import com.coderank.authservice.AuthService.entity.Users;
import com.coderank.authservice.AuthService.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Autowired
    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Users> user = authRepository.findByEmail(email);
        if (user.isPresent()) {
            var userObj = user.get();
            return new User(
                    userObj.getEmail(),
                    userObj.getPassword(),
                    List.of() // No roles/authorities
            );
        } else {
            throw new UsernameNotFoundException(email);
        }
    }

    public Users saveUser(Users user) {
        return authRepository.save(user);
    }

    public Optional<Users> findUserByEmail(String email) {
        return authRepository.findByEmail(email);
    }

    public boolean emailExists(String email) {
        return authRepository.findByEmail(email).isPresent();
    }
}
