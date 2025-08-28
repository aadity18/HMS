package com.example.reservation_service.dto;

import lombok.Data;

@Data
public class NotificationMessage {
    private String to;
    private String subject;
    private String body;
	
    
}
