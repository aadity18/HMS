package com.example.payment_service.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.payment_service.dto.BillDTO;

@FeignClient(name = "BILLING-SERVICE")
public interface BillingClient {
    @GetMapping("/api/bills/price/{reservationId}")
    Double getTotalPrice(@PathVariable("reservationId") Long id);

    @PostMapping("/api/bills/generate")
    BillDTO generateBill(@RequestBody Map<String, Long> request);
}
