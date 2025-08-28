package com.example.reservation_service.service;

import com.example.reservation_service.dto.GuestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "GUEST-SERVICE")
public interface GuestServiceClient {

    @GetMapping("/api/guests/get/{id}")
    GuestDTO getGuestById(@PathVariable Long id);
    
    @GetMapping("/api/guests/code/{code}")
    GuestDTO getGuestByCode(@PathVariable String code);
}
