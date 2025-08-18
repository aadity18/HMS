// StaffAlreadyExistsException.java
package com.hms.staff.exception;

public class StaffAlreadyExistsException extends RuntimeException {
    public StaffAlreadyExistsException(String message) {
        super(message);
    }
}
