package com.hms.transaction.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "RESERVATION-SERVICE")
public interface ReservationServiceClient {

    @PutMapping("/api/reservations/{id}/status")
    void updateReservationStatus(@PathVariable("id") Long reservationId,@RequestParam boolean paymentSuccess);
}
