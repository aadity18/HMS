package com.example.auth_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth_service.model.UserCredential;

import java.util.Optional;

public interface UserCredentialRepository  extends JpaRepository<UserCredential,Integer> {
    Optional<UserCredential> findByName(String username);
}