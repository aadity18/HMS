package com.hms.reservation.service;

import com.hms.reservation.dto.GuestDTO;
import com.hms.reservation.dto.ReservationStatusUpdateDTO;
import com.hms.reservation.dto.RoomDTO;
import com.hms.reservation.exception.*;
import com.hms.reservation.model.Reservation;
import com.hms.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    private RoomDTO sampleRoom;
    private GuestDTO sampleGuest;
    private Reservation sampleReservation;

    @BeforeEach
    void setUp() {
        sampleRoom = new RoomDTO();
        sampleRoom.setId(1L);
        sampleRoom.setCode("R001");
        sampleRoom.setCapacity(4);
        sampleRoom.setPrice(100.0);

        sampleGuest = new GuestDTO();
        sampleGuest.setId(1L);
        sampleGuest.setCode("G001");
        sampleGuest.setEmail("guest@test.com");

        sampleReservation = new Reservation(
                "RES001", "R001", "G001", 2, 1,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3),
                2, "PENDING", 200.0
        );
        sampleReservation.setId(1L);
        sampleReservation.setRoomId(1L);
        sampleReservation.setGuestId(1L);
    }

    @Test
    void createReservation_success() {
        when(reservationRepository.findOverlappingReservations(anyString(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(roomServiceClient.getRoomByCode("R001")).thenReturn(sampleRoom);
        when(guestServiceClient.getGuestByCode("G001")).thenReturn(sampleGuest);
        when(reservationRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(sampleReservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = reservationService.createReservation(
                "R001", "G001", 2, 1,
                LocalDate.now().plusDays(1).toString(),
                LocalDate.now().plusDays(3).toString()
        );

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        assertEquals(3, result.getNumberOfAdults()+ result.getNumberOfChildren());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void createReservation_roomNotAvailable_throwsException() {
        when(reservationRepository.findOverlappingReservations(anyString(), any(), any()))
                .thenReturn(Collections.singletonList(sampleReservation));

        assertThrows(RoomNotAvailableException.class, () -> reservationService.createReservation(
                "R001", "G001", 2, 1,
                LocalDate.now().plusDays(1).toString(),
                LocalDate.now().plusDays(3).toString()
        ));
    }

    @Test
    void createReservation_capacityExceeded_throwsException() {
        sampleRoom.setCapacity(2);
        when(reservationRepository.findOverlappingReservations(anyString(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(roomServiceClient.getRoomByCode("R001")).thenReturn(sampleRoom);

        assertThrows(RoomCapacityExceededException.class, () -> reservationService.createReservation(
                "R001", "G001", 3, 1,
                LocalDate.now().plusDays(1).toString(),
                LocalDate.now().plusDays(3).toString()
        ));
    }

    @Test
    void getReservationById_success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(sampleReservation));
        Reservation result = reservationService.getReservationById(1L);
        assertEquals("RES001", result.getCode());
    }

    @Test
    void getReservationById_notFound_throwsException() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ReservationNotFoundException.class, () -> reservationService.getReservationById(1L));
    }

    @Test
    void updateReservation_success() {
        Reservation updated = new Reservation(
                "RES001", "R001", "G001", 1, 1,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(4),
                2, "PENDING", 0.0
        );
        updated.setRoomId(1L);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(sampleReservation));
        when(roomServiceClient.getRoomById(1L)).thenReturn(sampleRoom);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = reservationService.updateReservation(1L, updated);
        assertEquals(2, result.getNumberOfNights());
        assertEquals(200.0, result.getTotalPrice());
    }

    @Test
    void deleteReservation_success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(sampleReservation));
        reservationService.deleteReservation(1L);
        verify(reservationRepository).delete(sampleReservation);
    }

//    @Test
//    void checkoutReservation_success() {
//        sampleReservation.setStatus("BOOKED");
//        ReservationStatusUpdateDTO dto = new ReservationStatusUpdateDTO();
//        dto.setStatus("CHECKED_OUT");
//
//        when(reservationRepository.findById(1L)).thenReturn(Optional.of(sampleReservation));
//        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        Reservation result = reservationService.checkoutReservation(1L, dto);
//        assertEquals("CHECKED_OUT", result.getStatus());
//    }

    @Test
    void updateReservationStatus_paymentSuccess() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(sampleReservation));
        when(guestServiceClient.getGuestById(1L)).thenReturn(sampleGuest);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = reservationService.updateReservationStatus(1L, true);

        assertEquals("BOOKED", result.getStatus());
        verify(notificationPublisher).sendNotification(eq("guest@test.com"), anyString(), contains("Reservation Code"));
    }

    @Test
    void updateReservationStatus_paymentFailed() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(sampleReservation));
        when(guestServiceClient.getGuestById(1L)).thenReturn(sampleGuest);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = reservationService.updateReservationStatus(1L, false);

        assertEquals("CANCELLED", result.getStatus());
        verify(notificationPublisher).sendNotification(eq("guest@test.com"), eq("Booking Failed"), anyString());
    }

    @Test
    void getAvailableRooms_success() {
        when(roomServiceClient.getAllRooms()).thenReturn(Collections.singletonList(sampleRoom));
        when(reservationRepository.findAll()).thenReturn(Collections.emptyList());

        List<RoomDTO> result = reservationService.getAvailableRooms(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );

        assertEquals(1, result.size());
        assertEquals("R001", result.get(0).getCode());
    }

    @Test
    void getAvailableRoomsFallback_throwsRoomServiceDownException() {
        assertThrows(RoomServiceDownException.class, () -> reservationService.getAvailableRoomsFallback(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                new RuntimeException("Service down")
        ));
    }
}
