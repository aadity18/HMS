package com.hms.auth.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hms.auth.model.UserCredential;
import com.hms.auth.repository.UserCredentialRepository;

@Service
public class AuthService {

    @Autowired
    private UserCredentialRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    public String saveUser(UserCredential credential) {
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        repository.save(credential);
        return "user added to the system";
    }

    public String getUserRole(String email) {
        UserCredential user = repository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRole();
    }

    public String generateToken(String email, String role) {
        return jwtService.generateToken(email, role);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }
}
