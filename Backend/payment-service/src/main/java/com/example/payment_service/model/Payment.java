package com.example.payment_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long reservationId;
    private Double amountPaid;
    private LocalDateTime paymentTime;
    private String razorpayOrderId; // For storing Razorpay order ID
    private boolean paymentSuccess;

   
}
