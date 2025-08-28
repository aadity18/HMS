package com.hms.reservation.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hms.reservation.dto.RoomDTO;

@FeignClient(name = "ROOM-SERVICE")
public interface RoomServiceClient {

    @GetMapping("api/rooms/{id}")
    RoomDTO getRoomById(@PathVariable Long id);
    
    @GetMapping("api/rooms")
    List<RoomDTO> getAllRooms();
    
    @GetMapping("api/rooms/code/{code}")
    RoomDTO getRoomByCode(@PathVariable String code);


}
