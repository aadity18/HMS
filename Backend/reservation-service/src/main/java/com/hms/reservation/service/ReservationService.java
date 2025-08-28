package com.hms.reservation.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.reservation.dto.GuestDTO;
import com.hms.reservation.dto.ReservationStatusUpdateDTO;
import com.hms.reservation.dto.RoomDTO;
import com.hms.reservation.exception.GuestNotFoundException;
import com.hms.reservation.exception.ReservationNotFoundException;
import com.hms.reservation.exception.RoomCapacityExceededException;
import com.hms.reservation.exception.RoomNotAvailableException;
import com.hms.reservation.exception.RoomServiceDownException;
import com.hms.reservation.model.Reservation;
import com.hms.reservation.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private RoomServiceClient roomServiceClient;
    @Autowired
    private GuestServiceClient guestServiceClient;
    @Autowired
    private NotificationPublisher notificationPublisher;

    /* ---------- helpers ---------- */
    public Long getLastReservationId() {
        return reservationRepository.findTopByOrderByIdDesc()
                .map(Reservation::getId)
                .orElse(0L);
    }

    /* ---------- business use-cases ---------- */
    public Reservation createReservation(String roomCode,
                                         String guestCode,
                                         Integer numberOfAdults,
                                         Integer numberOfChildren,
                                         String checkInDate,
                                         String checkOutDate) {

        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);

        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Booking for past dates is not allowed.");
        }

        List<Reservation> overlapping = reservationRepository
                .findOverlappingReservations(roomCode, checkIn, checkOut)
                .stream()
                .filter(r -> "BOOKED".equals(r.getStatus()) || "PENDING".equals(r.getStatus()))
                .collect(Collectors.toList());
        if (!overlapping.isEmpty()) {
            throw new RoomNotAvailableException("Room is already booked or pending for the selected dates.");
        }

        RoomDTO room = getRoomByCode(roomCode);
        int totalGuests = numberOfAdults + numberOfChildren;
        if (totalGuests > room.getCapacity()) {
            throw new RoomCapacityExceededException("Room capacity exceeded");
        }

        GuestDTO guest = getGuestByCode(guestCode);
        if (guest == null) {
            throw new GuestNotFoundException("Guest not found");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        Long nextId = getLastReservationId() + 1;
        String reservationCode = "RES" + String.format("%03d", nextId);

        Reservation reservation = new Reservation(
                reservationCode,
                roomCode,
                guestCode,
                numberOfAdults,
                numberOfChildren,
                checkIn,
                checkOut,
                (int) nights,
                "PENDING",
                room.getPrice() * nights
        );

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
    }

    public Reservation getReservationByCode(String code) {
        return reservationRepository.findByCode(code)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with code: " + code));
    }

    public Reservation updateReservation(Long id, Reservation updated) {
        Reservation existing = getReservationById(id);
        existing.setNumberOfAdults(updated.getNumberOfAdults());
        existing.setNumberOfChildren(updated.getNumberOfChildren());
        existing.setCheckInDate(updated.getCheckInDate());
        existing.setCheckOutDate(updated.getCheckOutDate());

        long nights = ChronoUnit.DAYS.between(updated.getCheckInDate(), updated.getCheckOutDate());
        RoomDTO room = getRoomById(updated.getRoomId());
        existing.setNumberOfNights((int) nights);
        existing.setTotalPrice(room.getPrice() * nights);
        return reservationRepository.save(existing);
    }

    public void deleteReservation(Long id) {
        Reservation reservation = getReservationById(id);
        reservationRepository.delete(reservation);
    }

    public Reservation checkoutReservation(Long id, ReservationStatusUpdateDTO dto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        if (!"BOOKED".equals(reservation.getStatus())) {
            throw new IllegalStateException("Cannot checkout a reservation that is not booked");
        }
        reservation.setStatus(dto.getStatus());
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationByGuestId(Long guestId) {
        List<Reservation> reservations = reservationRepository.findByGuestId(guestId);
        if (reservations.isEmpty()) {
            throw new ReservationNotFoundException("Reservation not found");
        }
        return reservations;
    }

    public Reservation updateReservationStatus(Long id, boolean paymentSuccess) {
        Reservation reservation = getReservationById(id);

        GuestDTO guest = null;
        try {
            if (reservation.getGuestId() != null) {
                guest = getGuestById(reservation.getGuestId());
            } else if (reservation.getGuestCode() != null) {
                guest = getGuestByCode(reservation.getGuestCode());
            } else {
                throw new GuestNotFoundException("No guest information available for this reservation");
            }
        } catch (Exception e) {
            throw new GuestNotFoundException("Failed to fetch guest for reservation: " + id);
        }

        if (paymentSuccess) {
            reservation.setStatus("BOOKED");
            String subject = "Reservation Confirmed – Thank You!";
            String body =
                    "Your reservation has been successfully confirmed.\n\n" +
                    "Reservation Code: " + reservation.getCode() + "\n" +
                    "Room Code       : " + reservation.getRoomCode() + "\n" +
                    "Check-In Date   : " + reservation.getCheckInDate() + "\n" +
                    "Check-Out Date  : " + reservation.getCheckOutDate() + "\n" +
                    "We look forward to welcoming you and wish you a pleasant stay!\n\n" +
                    "Warm regards,\nHotel Management Team";
            notificationPublisher.sendNotification(guest.getEmail(), subject, body);
        } else {
            reservation.setStatus("CANCELLED");
            notificationPublisher.sendNotification(
                    guest.getEmail(),
                    "Booking Failed",
                    "Your booking for Room ID: " + reservation.getRoomId() + " has been cancelled due to payment failure.");
        }
        return reservationRepository.save(reservation);
    }

    public List<Long> getBookedRoomIds(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Past date check-in is not allowed.");
        }
        return reservationRepository.findAll().stream()
                .filter(r -> ("PENDING".equals(r.getStatus()) || "BOOKED".equals(r.getStatus()))
                        && r.getCheckOutDate().isAfter(checkInDate)
                        && r.getCheckInDate().isBefore(checkOutDate))
                .map(Reservation::getRoomId)
                .distinct()
                .collect(Collectors.toList());
    }

    @CircuitBreaker(name = "room-service", fallbackMethod = "getAvailableRoomsFallback")
    public List<RoomDTO> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Past date check-in is not allowed.");
        }

        List<RoomDTO> allRooms = getAllRooms();
        Set<String> blockedCodes = reservationRepository.findAll()
                .stream()
                .filter(r -> r.getStatus() != null
                        && (r.getStatus().equalsIgnoreCase("PENDING") || r.getStatus().equalsIgnoreCase("BOOKED"))
                        && r.getCheckOutDate().isAfter(checkInDate)
                        && r.getCheckInDate().isBefore(checkOutDate))
                .map(Reservation::getRoomCode)
                .collect(Collectors.toSet());

        return allRooms.stream()
                .filter(room -> room.getCode() != null && !blockedCodes.contains(room.getCode()))
                .collect(Collectors.toList());
    }

    public List<RoomDTO> getAvailableRoomsFallback(LocalDate checkInDate,
                                                    LocalDate checkOutDate,
                                                    Throwable ex) {
        throw new RoomServiceDownException("Room-service is down – Try again later");
    }

    /* ---------- low-level remote calls with their own CB ---------- */
    @CircuitBreaker(name = "room-service", fallbackMethod = "roomServiceFallback")
    public RoomDTO getRoomByCode(String roomCode) {
        return roomServiceClient.getRoomByCode(roomCode);
    }

    @CircuitBreaker(name = "room-service", fallbackMethod = "roomServiceFallback")
    public RoomDTO getRoomById(Long roomId) {
        return roomServiceClient.getRoomById(roomId);
    }

    @CircuitBreaker(name = "room-service", fallbackMethod = "roomServiceFallback")
    public List<RoomDTO> getAllRooms() {
        return roomServiceClient.getAllRooms();
    }

    @CircuitBreaker(name = "guest-service", fallbackMethod = "guestServiceFallback")
    public GuestDTO getGuestByCode(String guestCode) {
        return guestServiceClient.getGuestByCode(guestCode);
    }

    @CircuitBreaker(name = "guest-service", fallbackMethod = "guestServiceFallback")
    public GuestDTO getGuestById(Long guestId) {
        return guestServiceClient.getGuestById(guestId);
    }

    /* ---------- fallbacks for low-level calls ---------- */
    private RoomDTO roomServiceFallback(String codeOrId, Exception ex) {
        throw new IllegalStateException("Room service is unavailable", ex);
    }
    private List<RoomDTO> roomServiceFallback(Exception ex) {
        throw new IllegalStateException("Room service is unavailable", ex);
    }
    private GuestDTO guestServiceFallback(Object idOrCode, Exception ex) {
        throw new IllegalStateException("Guest service is unavailable", ex);
    }
}