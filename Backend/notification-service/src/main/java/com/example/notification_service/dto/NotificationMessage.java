package com.example.notification_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationMessage {
    private String to;
    private String subject;
    private String body;
	
    
}
