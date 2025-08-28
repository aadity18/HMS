package com.hms.reservation.dto;

import lombok.Data;

@Data
public class NotificationMessage {
    private String to;
    private String subject;
    private String body;
	
    
}
