package com.hms.rooms.controller;

import com.hms.rooms.entity.Room;
import com.hms.rooms.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Room Management", description = "APIs for managing hotel rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "Get all rooms", description = "Retrieves a list of all rooms in the system.")
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @Operation(summary = "Get a room by ID", description = "Retrieves a room by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@Parameter(description = "ID of the room to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @Operation(summary = "Get a room by Code", description = "Retrieves a room by its unique code.")
    @GetMapping("/code/{code}")
    public ResponseEntity<Room> getRoomByCode(@Parameter(description = "Code of the room to retrieve") @PathVariable String code) {
        return ResponseEntity.ok(roomService.getRoomByCode(code));
    }

    @Operation(summary = "Add a new room", description = "Adds a new room to the system.")
    @PostMapping
    public ResponseEntity<Room> addRoom(@Parameter(description = "Room object to be added") @RequestBody Room room) {
        return ResponseEntity.ok(roomService.addRoom(room));
    }

    @Operation(summary = "Update a room", description = "Updates an existing room by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@Parameter(description = "ID of the room to update") @PathVariable Long id, 
                                          @Parameter(description = "Updated room object") @RequestBody Room room) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    @Operation(summary = "Delete a room", description = "Deletes a room by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@Parameter(description = "ID of the room to delete") @PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get available rooms", description = "Retrieves a list of all available rooms.")
    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    @Operation(summary = "Set room rate", description = "Sets the rate for a room by its ID.")
    @PutMapping("/setRate/{id}")
    public ResponseEntity<Room> setRate(@Parameter(description = "ID of the room to set rate for") @PathVariable Long id, 
                                       @Parameter(description = "New rate for the room") @RequestBody Double rate) {
        return ResponseEntity.ok(roomService.setRate(id, rate));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
