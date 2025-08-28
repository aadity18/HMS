package com.hms.transaction.model;

import java.time.LocalDate;
import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String billNo;
    private Long reservationId;
    @Column(name = "reservation_code")
    private String reservationCode;
    private String guestCode;
    private String roomCode;
    private Double baseAmount;
    private Double taxAmount;
    private Double totalAmount;
    private LocalDate date;
	
}