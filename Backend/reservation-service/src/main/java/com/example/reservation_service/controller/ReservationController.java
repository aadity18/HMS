package com.example.reservation_service.controller;

import com.example.reservation_service.dto.ReservationStatusUpdateDTO;
import com.example.reservation_service.dto.RoomDTO;
import com.example.reservation_service.model.Reservation;
import com.example.reservation_service.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/create")
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation request) {
        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Reservation reservation = reservationService.createReservation(
                request.getRoomCode(),
                request.getGuestCode(),
                request.getNumberOfAdults(),
                request.getNumberOfChildren(),
                request.getCheckInDate().toString(),
                request.getCheckOutDate().toString()
        );
        return ResponseEntity.status(201).body(reservation);
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    // ✅ Available rooms first (avoid clash with /{id})
    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate
    ) {
        if (checkInDate.isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().build();
        }
        List<RoomDTO> availableRooms = reservationService.getAvailableRooms(checkInDate, checkOutDate);
        return ResponseEntity.ok(availableRooms);
    }

    @GetMapping("/booked-room-ids")
    public List<Long> getBookedRoomIds(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate
    ) {
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Past date check-in is not allowed.");
        }
        return reservationService.getBookedRoomIds(checkInDate, checkOutDate);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    // ✅ New endpoint to fetch reservation by code
    @GetMapping("/code/{code}")
    public ResponseEntity<Reservation> getReservationByCode(@PathVariable String code) {
        return ResponseEntity.ok(reservationService.getReservationByCode(code));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation updated) {
        return ResponseEntity.ok(reservationService.updateReservation(id, updated));
    }

    @PatchMapping("/{id}/checkout")
    public ResponseEntity<Reservation> checkoutReservation(@PathVariable Long id, @RequestBody ReservationStatusUpdateDTO reservation) {
        return new ResponseEntity<>(reservationService.checkoutReservation(id, reservation), HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Reservation> updateReservationStatus(@PathVariable Long id, @RequestParam boolean paymentSuccess) {
        return new ResponseEntity<>(reservationService.updateReservationStatus(id, paymentSuccess), HttpStatus.OK);
    }

    @GetMapping("/{guestId}/history")
    public ResponseEntity<List<Reservation>> getReservationByGuestId(@PathVariable Long guestId) {
        return new ResponseEntity<>(reservationService.getReservationByGuestId(guestId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
