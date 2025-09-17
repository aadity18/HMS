package com.hms.transaction.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationDTO {
    private Long reservationId;
    private String code;
    private String guestCode;
    private String roomCode;
    private Integer numberOfNights;
    private Double totalPrice;
}