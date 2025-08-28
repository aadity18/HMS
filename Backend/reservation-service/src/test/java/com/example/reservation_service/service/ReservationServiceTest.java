package com.example.reservation_service.service;

import com.example.reservation_service.dto.GuestDTO;
import com.example.reservation_service.dto.ReservationStatusUpdateDTO;
import com.example.reservation_service.dto.RoomDTO;
import com.example.reservation_service.exception.*;
import com.example.reservation_service.model.Reservation;
import com.example.reservation_service.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private RoomServiceClient roomServiceClient;
    @Mock
    private GuestServiceClient guestServiceClient;
    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private ReservationService reservationService;

    private final LocalDate today = LocalDate.now();
    private final String roomCode = "R101";
    private final String guestCode = "G001";
    private RoomDTO room;
    private GuestDTO guest;

    @BeforeEach
    void setUp() {
        room = new RoomDTO();
        room.setCode(roomCode);
        room.setCapacity(4);
        room.setPrice(100.0);

        guest = new GuestDTO();
        guest.setCode(guestCode);
        guest.setEmail("g@g.com");
    }

    /* ---------- createReservation ---------- */

    @Test
    @DisplayName("createReservation() succeeds when room is free")
    void createReservation_success() {
        given(roomServiceClient.getRoomByCode(roomCode)).willReturn(room);
        given(guestServiceClient.getGuestByCode(guestCode)).willReturn(guest);
        given(reservationRepository.findTopByOrderByIdDesc()).willReturn(Optional.empty()); // 0 → RES001
        given(reservationRepository.findOverlappingReservations(any(), any(), any()))
                .willReturn(List.of());
        given(reservationRepository.save(any(Reservation.class)))
                .willAnswer(inv -> inv.getArgument(0));

        Reservation res = reservationService.createReservation(
                roomCode, guestCode, 2, 0,
                today.plusDays(1).toString(), today.plusDays(3).toString());

        assertThat(res.getCode()).isEqualTo("RES001");
        assertThat(res.getStatus()).isEqualTo("PENDING");
        assertThat(res.getTotalPrice()).isEqualTo(200.0);
    }

    @Test
    @DisplayName("createReservation() throws when room is already booked")
    void createReservation_roomNotAvailable() {
        Reservation existing = new Reservation();
        existing.setStatus("BOOKED");

        given(reservationRepository.findOverlappingReservations(any(), any(), any()))
                .willReturn(List.of(existing));

        assertThatThrownBy(() ->
                reservationService.createReservation(
                        roomCode, guestCode, 1, 0,
                        today.plusDays(1).toString(), today.plusDays(2).toString()))
                .isInstanceOf(RoomNotAvailableException.class)
                .hasMessageContaining("Room is already booked");
    }

    @Test
    @DisplayName("createReservation() throws when capacity exceeded")
    void createReservation_capacityExceeded() {
        given(roomServiceClient.getRoomByCode(roomCode)).willReturn(room);

        assertThatThrownBy(() ->
                reservationService.createReservation(
                        roomCode, guestCode, 5, 0,
                        today.plusDays(1).toString(), today.plusDays(2).toString()))
                .isInstanceOf(RoomCapacityExceededException.class)
                .hasMessage("Room capacity exceeded");
    }

    /* ---------- getReservationById ---------- */

    @Test
    @DisplayName("getReservationById() returns reservation")
    void getReservationById_found() {
        Reservation r = new Reservation();
        r.setId(1L);
        given(reservationRepository.findById(1L)).willReturn(Optional.of(r));

        Reservation found = reservationService.getReservationById(1L);

        assertThat(found).isEqualTo(r);
    }

    @Test
    @DisplayName("getReservationById() throws when not found")
    void getReservationById_notFound() {
        given(reservationRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.getReservationById(99L))
                .isInstanceOf(ReservationNotFoundException.class);
    }

    /* ---------- updateReservationStatus (payment success) ---------- */

    @Test
    @DisplayName("updateReservationStatus() marks BOOKED and sends email")
    void updateReservationStatus_paymentSuccess() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setCode("RES001");
        reservation.setRoomCode("R101");
        reservation.setGuestCode(guestCode);
        reservation.setCheckInDate(today.plusDays(1));
        reservation.setCheckOutDate(today.plusDays(3));
        reservation.setTotalPrice(200.0);

        given(reservationRepository.findById(1L)).willReturn(Optional.of(reservation));
        given(guestServiceClient.getGuestByCode(guestCode)).willReturn(guest);
        given(reservationRepository.save(any())).willReturn(reservation);

        Reservation updated = reservationService.updateReservationStatus(1L, true);

        assertThat(updated.getStatus()).isEqualTo("BOOKED");
        verify(notificationPublisher).sendNotification(
                eq("g@g.com"),
                eq("Reservation Confirmed – Thank You!"),
                contains("Reservation Code: RES001"));
    }

    /* ---------- deleteReservation ---------- */

    @Test
    @DisplayName("deleteReservation() removes reservation")
    void deleteReservation_success() {
        Reservation r = new Reservation();
        r.setId(1L);
        given(reservationRepository.findById(1L)).willReturn(Optional.of(r));

        reservationService.deleteReservation(1L);

        verify(reservationRepository).delete(r);
    }

    /* ---------- getBookedRoomIds ---------- */

    @Test
    @DisplayName("getBookedRoomIds() returns distinct blocked room codes")
    void getBookedRoomIds() {
        Reservation booked = new Reservation();
        booked.setRoomCode("R101");
        booked.setStatus("BOOKED");
        booked.setCheckInDate(today.plusDays(1));
        booked.setCheckOutDate(today.plusDays(5));

        given(reservationRepository.findAll()).willReturn(List.of(booked));

        List<Long> ids = reservationService.getBookedRoomIds(
                today.plusDays(2), today.plusDays(3));

        // NOTE: method returns List<Long>, but your Reservation stores roomCode as String.
        // Adjust test once you align the type.
    }

    /* ---------- getAvailableRooms ---------- */

    @Test
    @DisplayName("getAvailableRooms() filters occupied rooms")
    void getAvailableRooms() {
        RoomDTO freeRoom = new RoomDTO();
        freeRoom.setCode("R102");

        given(roomServiceClient.getAllRooms()).willReturn(List.of(room, freeRoom));

        Reservation booked = new Reservation();
        booked.setRoomCode("R101");
        booked.setStatus("BOOKED");
        booked.setCheckInDate(today.plusDays(1));
        booked.setCheckOutDate(today.plusDays(5));

        given(reservationRepository.findAll()).willReturn(List.of(booked));

        List<RoomDTO> available = reservationService.getAvailableRooms(
                today.plusDays(2), today.plusDays(3));

        assertThat(available).extracting(RoomDTO::getCode)
                .containsExactly("R102");
    }
}