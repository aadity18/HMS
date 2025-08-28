package com.example.billing_service.exception;

public class ReservationNotFoundException extends Exception {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
