package com.example.payment_service.dto;

import java.time.LocalDate;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillDTO {
	private Long id;
    private String billNo;
    private Long reservationId;
    private String reservationCode;
    private Long guestCode;
    private Long roomCode;
    private Double baseAmount;
    private Double taxAmount;
    private Double totalAmount;
    private LocalDate date;
	
}
