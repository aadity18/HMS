package com.example.reservation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestDTO {
	private Long id;
	private String code;
	private String name;
	private String email;
	
}
