package com.example.reservation_service.exception;

public class RoomCapacityExceededException extends RuntimeException {
    public RoomCapacityExceededException(String message) {
        super(message);
    }
}