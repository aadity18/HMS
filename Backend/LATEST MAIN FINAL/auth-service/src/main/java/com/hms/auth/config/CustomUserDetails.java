package com.hms.auth.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hms.auth.model.UserCredential;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private String role;

    public CustomUserDetails(UserCredential userCredential) {
        this.username = userCredential.getName();
        this.password = userCredential.getPassword();
        this.role = userCredential.getRole(); 
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)); // Ensure role is properly prefixed
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
    
    public String getRole() {
    	return role;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
