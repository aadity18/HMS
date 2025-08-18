package com.hms.staff.controller;

import com.hms.staff.entity.Staff;
import com.hms.staff.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@Tag(name = "Staff Management", description = "APIs for managing hotel staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    // New endpoint to fetch the last staff ID
    @Operation(summary = "Get last staff ID", description = "Returns the last staff ID for code generation")
    @GetMapping("/last-id")
    public ResponseEntity<Long> getLastStaffId() {
        return ResponseEntity.ok(staffService.getLastStaffId());
    }

    @Operation(summary = "Get all staff")
    @GetMapping("/all")
    public ResponseEntity<List<Staff>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @Operation(summary = "Get a staff by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Staff> getStaffById(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    @Operation(summary = "Get a staff by code")
    @GetMapping("/code/{code}")
    public ResponseEntity<Staff> getStaffByCode(@PathVariable String code) {
        return ResponseEntity.ok(staffService.getStaffByCode(code));
    }

    @Operation(summary = "Add new staff (auto-generates code if not provided)")
    @PostMapping("/add")
    public ResponseEntity<Staff> addStaff(@RequestBody Staff staff) {
        return ResponseEntity.ok(staffService.addStaff(staff));
    }

    @Operation(summary = "Update staff")
    @PutMapping("/{id}")
    public ResponseEntity<Staff> updateStaff(@PathVariable Long id, @RequestBody Staff staff) {
        return ResponseEntity.ok(staffService.updateStaff(id, staff));
    }

    @Operation(summary = "Delete staff")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}