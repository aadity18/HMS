package com.hms.transaction.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.hms.transaction.dto.ReservationDTO;

@FeignClient(name = "reservation-service")
public interface ReservationClient {
    @GetMapping("/api/reservations/{id}")
    ReservationDTO getReservationById(@PathVariable Long id);
    
    @GetMapping("/api/reservations/code/{code}")
    ReservationDTO getReservationByCode(@PathVariable String code);    
}