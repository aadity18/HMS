package com.hms.reservation.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.hms.reservation.dto.GuestDTO;

@FeignClient(name = "GUEST-SERVICE")
public interface GuestServiceClient {

    @GetMapping("/api/guests/get/{id}")
    GuestDTO getGuestById(@PathVariable Long id);
    
    @GetMapping("/api/guests/code/{code}")
    GuestDTO getGuestByCode(@PathVariable String code);
}
