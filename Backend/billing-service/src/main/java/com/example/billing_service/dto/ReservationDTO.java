package com.example.billing_service.dto;

import lombok.Data;

@Data
public class ReservationDTO {
    private Long reservationId;
    private String code;
    private String guestCode;
    private String roomCode;
    private Integer numberOfNights;
    private Double totalPrice;
	
}