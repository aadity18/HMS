package com.hms.auth.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.auth.model.UserCredential;

import java.util.Optional;

public interface UserCredentialRepository  extends JpaRepository<UserCredential,Integer> {
    //Optional<UserCredential> findByName(String username);
    Optional<UserCredential> findByEmail(String email);
}