package com.hms.guests.controller;

import com.hms.guests.entity.Guest;
import com.hms.guests.service.GuestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
@Tag(name = "Guest Management", description = "APIs for managing hotel guests")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    // New endpoint to fetch the last guest ID
    @Operation(summary = "Get last guest ID", description = "Returns the last guest ID for code generation")
    @GetMapping("/last-id")
    public ResponseEntity<Long> getLastGuestId() {
        return ResponseEntity.ok(guestService.getLastGuestId());
    }

    // Existing endpoints
    @Operation(summary = "Get all guests")
    @GetMapping("/get")
    public ResponseEntity<List<Guest>> getAllGuests() {
        return ResponseEntity.ok(guestService.getAllGuests());
    }

    @Operation(summary = "Get guest by ID")
    @GetMapping("/get/{id}")
    public ResponseEntity<Guest> getGuestById(@PathVariable Long id) {
        return ResponseEntity.ok(guestService.getGuestById(id));
    }
    
    @Operation(summary = "Get a guest by Code", description = "Retrieves a guest by its unique code.")
    @GetMapping("/code/{code}")
    public ResponseEntity<Guest> getGuestByCode(@Parameter(description = "Code of the guest to retrieve") @PathVariable String code) {
        return ResponseEntity.ok(guestService.getGuestByCode(code));
    }

    // ✅ New: Get guest by phone number
    @Operation(summary = "Get guest by phone number")
    @GetMapping("/get/phone/{phoneNumber}")
    public ResponseEntity<Guest> getGuestByPhoneNumber(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(guestService.getGuestByPhoneNumber(phoneNumber));
    }

    @Operation(summary = "Add new guest (auto-generates code if not provided)")
    @PostMapping("/add")
    public ResponseEntity<Guest> addGuest(@RequestBody Guest guest) {
        return ResponseEntity.ok(guestService.addGuest(guest));
    }

    @Operation(summary = "Update guest")
    @PutMapping("/edit/{id}")
    public ResponseEntity<Guest> updateGuest(@PathVariable Long id, @RequestBody Guest guest) {
        return ResponseEntity.ok(guestService.updateGuest(id, guest));
    }

    @Operation(summary = "Delete guest")
    @DeleteMapping("/edit/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
