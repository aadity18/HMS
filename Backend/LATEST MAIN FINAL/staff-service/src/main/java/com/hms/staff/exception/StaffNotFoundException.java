// StaffNotFoundException.java
package com.hms.staff.exception;

public class StaffNotFoundException extends RuntimeException {
    public StaffNotFoundException(String message) {
        super(message);
    }
}
