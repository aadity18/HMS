package com.hms.reservation.exception;

public class RoomServiceDownException extends RuntimeException {
    public RoomServiceDownException(String msg) {
        super(msg);
    }
}