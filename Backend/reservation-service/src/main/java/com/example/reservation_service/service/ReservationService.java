package com.example.reservation_service.service;

import com.example.reservation_service.dto.GuestDTO;
import com.example.reservation_service.dto.ReservationStatusUpdateDTO;
import com.example.reservation_service.dto.RoomDTO;
import com.example.reservation_service.exception.RoomNotAvailableException;
import com.example.reservation_service.exception.GuestNotFoundException;
import com.example.reservation_service.exception.ReservationNotFoundException;
import com.example.reservation_service.exception.RoomCapacityExceededException;
import com.example.reservation_service.model.Reservation;
import com.example.reservation_service.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    // ✅ New method to get the last Reservation ID
    public Long getLastReservationId() {
        return reservationRepository.findTopByOrderByIdDesc()
                .map(Reservation::getId)
                .orElse(0L); // Default 0 if no reservations exist
    }

    // ✅ Updated createReservation with auto-code generation
    public Reservation createReservation(String roomCode,
                                         String guestCode,
                                         Integer numberOfAdults,
                                         Integer numberOfChildren,
                                         String checkInDate,
                                         String checkOutDate) {

        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);

        // Prevent past bookings
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Booking for past dates is not allowed.");
        }

        // 1️⃣ Check overlapping reservations (BOOKED or PENDING)
        List<Reservation> overlappingReservations = reservationRepository
                .findOverlappingReservations(roomCode, checkIn, checkOut)
                .stream()
                .filter(res -> "BOOKED".equals(res.getStatus()) || "PENDING".equals(res.getStatus()))
                .collect(Collectors.toList());
        if (!overlappingReservations.isEmpty()) {
            throw new RoomNotAvailableException("Room is already booked or pending for the selected dates.");
        }

        // 2️⃣ Validate room capacity
        RoomDTO room = roomServiceClient.getRoomByCode(roomCode);
        int totalGuests = numberOfAdults + numberOfChildren;
        if (totalGuests > room.getCapacity()) {
            throw new RoomCapacityExceededException("Room capacity exceeded");
        }

        // 3️⃣ Validate guest existence
        GuestDTO guest = guestServiceClient.getGuestByCode(guestCode);
        if (guest == null) {
            throw new GuestNotFoundException("Guest not found");
        }

        // 4️⃣ Calculate nights and total price
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // 5️⃣ Auto-generate Reservation Code if null/empty
        Long nextId = getLastReservationId() + 1;
        String reservationCode = "RES" + String.format("%03d", nextId);

        Reservation reservation = new Reservation(
                reservationCode, // <-- Auto-generated ReservationCode
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

    // 🔹 Fetch all reservations
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // 🔹 Get reservation by ID
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
    }

    // 🔹 Get reservation by Code
    public Reservation getReservationByCode(String code) {
        return reservationRepository.findByCode(code)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with code: " + code));
    }

    // 🔹 Update reservation details
    public Reservation updateReservation(Long id, Reservation updated) {
        Reservation existing = getReservationById(id);
        if (existing == null) {
            throw new ReservationNotFoundException("Reservation not found");
        }
        existing.setNumberOfAdults(updated.getNumberOfAdults());
        existing.setNumberOfChildren(updated.getNumberOfChildren());
        existing.setCheckInDate(updated.getCheckInDate());
        existing.setCheckOutDate(updated.getCheckOutDate());
        long nights = ChronoUnit.DAYS.between(updated.getCheckInDate(), updated.getCheckOutDate());
        RoomDTO room = roomServiceClient.getRoomById(updated.getRoomId());
        existing.setNumberOfNights((int) nights);
        existing.setTotalPrice(room.getPrice() * nights);
        return reservationRepository.save(existing);
    }

    // 🔹 Delete reservation
    public void deleteReservation(Long id) {
        Reservation reservation = getReservationById(id);
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation not found");
        }
        reservationRepository.delete(reservation);
    }

    // 🔹 Checkout reservation
    public Reservation checkoutReservation(Long id, ReservationStatusUpdateDTO reservationDto) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);
        if (reservationOptional.isEmpty()) {
            throw new ReservationNotFoundException("Reservation not found");
        }
        Reservation reservation = reservationOptional.get();
        if (!"BOOKED".equals(reservation.getStatus())) {
            throw new IllegalStateException("Cannot checkout a reservation that is not booked");
        }
        reservation.setStatus(reservationDto.getStatus());
        return reservationRepository.save(reservation);
    }

    // 🔹 Get reservations by Guest ID
    public List<Reservation> getReservationByGuestId(Long guestId) {
        List<Reservation> reservation = reservationRepository.findByGuestId(guestId);
        if (reservation.isEmpty()) {
            throw new ReservationNotFoundException("Reservation not found");
        }
        return reservation;
    }

    // 🔹 Update reservation after payment
    public Reservation updateReservationStatus(Long id, boolean paymentSuccess) {
        System.out.println("[DEBUG] updateReservationStatus called for reservationId: " + id);

        // Fetch reservation
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    System.out.println("[ERROR] Reservation not found for id: " + id);
                    return new ReservationNotFoundException("Reservation not found");
                });

        System.out.println("[DEBUG] Reservation found: " + reservation);

        GuestDTO guest = null;
        try {
            if (reservation.getGuestId() != null) {
                System.out.println("[DEBUG] Fetching guest by ID: " + reservation.getGuestId());
                guest = guestServiceClient.getGuestById(reservation.getGuestId());
            } else if (reservation.getGuestCode() != null) {
                System.out.println("[DEBUG] Fetching guest by Code: " + reservation.getGuestCode());
                guest = guestServiceClient.getGuestByCode(reservation.getGuestCode());
            } else {
                throw new GuestNotFoundException("No guest information available for this reservation");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GuestNotFoundException("Failed to fetch guest for reservation: " + id);
        }

        // Update status
        if (paymentSuccess) {
            reservation.setStatus("BOOKED");
//            notificationPublisher.sendNotification(
//                    guest.getEmail(),
//                    "Booking Confirmed",
//                    "Your booking for Room ID: " + reservation.getRoomId() + " has been confirmed."
//            );
         // --- inside updateReservationStatus (after paymentSuccess = true) ---
            String subject = "Reservation Confirmed – Thank You!";

            String body =
               // "Dear " + guest.getName() + ",\n\n" +
                "Your reservation has been successfully confirmed.\n\n" +
                "Reservation Details:\n" +
                "Reservation Code: " + reservation.getCode() + "\n" +
                "Room Code       : " + reservation.getRoomCode() + "\n" +
                "Check-In Date   : " + reservation.getCheckInDate() + "\n" +
                "Check-Out Date  : " + reservation.getCheckOutDate() + "\n" +
                "Total Price     : ₹" + reservation.getTotalPrice() + "\n\n" +
                "We look forward to welcoming you and wish you a pleasant stay!\n\n" +
                "Warm regards,\n" +
                "Hotel Management Team";

            notificationPublisher.sendNotification(guest.getEmail(), subject, body);
        } else {
            reservation.setStatus("CANCELLED");
            notificationPublisher.sendNotification(
                    guest.getEmail(),
                    "Booking Failed",
                    "Your booking for Room ID: " + reservation.getRoomId() + " has been cancelled due to payment failure."
            );
        }
        return reservationRepository.save(reservation);
    }


    // 🔹 Get list of booked room IDs for given dates
    public List<Long> getBookedRoomIds(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Past date check-in is not allowed.");
        }
        List<Reservation> allReservations = reservationRepository.findAll();
        return allReservations.stream()
                .filter(reservation ->
                        (reservation.getStatus().equals("PENDING") || reservation.getStatus().equals("BOOKED")) &&
                                reservation.getCheckOutDate().isAfter(checkInDate) &&
                                reservation.getCheckInDate().isBefore(checkOutDate)
                )
                .map(Reservation::getRoomId)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<RoomDTO> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Past date check-in is not allowed.");
        }

        // Fetch all rooms from Room service
        List<RoomDTO> allRooms = roomServiceClient.getAllRooms();

        // Fetch all reservations and compute blocked room IDs by overlapping and status
        List<Reservation> allReservations = reservationRepository.findAll();

        Set<String> blockedRoomCodes = allReservations.stream()
                .filter(r -> r.getStatus() != null &&
                        (r.getStatus().equalsIgnoreCase("PENDING") || r.getStatus().equalsIgnoreCase("BOOKED")) &&
                        r.getCheckOutDate().isAfter(checkInDate) &&
                        r.getCheckInDate().isBefore(checkOutDate)
                )
                .map(Reservation::getRoomCode) // 🔹 use code if reservation stores codes
                .collect(Collectors.toSet());

        return allRooms.stream()
                .filter(room -> room.getCode() != null && !blockedRoomCodes.contains(room.getCode()))
                .collect(Collectors.toList());
    }
}
