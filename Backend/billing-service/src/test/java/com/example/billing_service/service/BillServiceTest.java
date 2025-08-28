package com.example.billing_service.service;

import com.example.billing_service.dto.ReservationDTO;
import com.example.billing_service.exception.ReservationNotFoundException;
import com.example.billing_service.model.Bill;
import com.example.billing_service.repository.BillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock
    private BillRepository billRepository;
    @Mock
    private ReservationClient reservationClient;

    @InjectMocks
    private BillService billService;

    private final Long reservationId = 10L;
    private final Double baseAmount = 2000.0;

    private ReservationDTO reservationDTO;

    @BeforeEach
    void setUp() {
        reservationDTO = new ReservationDTO();
        reservationDTO.setReservationId(reservationId);
        reservationDTO.setCode("RES010");
        reservationDTO.setGuestCode("G001");
        reservationDTO.setRoomCode("R101");
        reservationDTO.setTotalPrice(baseAmount);
    }

    /* ---------- calculateTotalPrice ---------- */

    @Test
    @DisplayName("calculateTotalPrice() returns base + 18 % tax")
    void calculateTotalPrice_success() throws ReservationNotFoundException {
        given(reservationClient.getReservationById(reservationId)).willReturn(reservationDTO);

        Double total = billService.calculateTotalPrice(reservationId);

        assertThat(total).isEqualTo(baseAmount * 1.18);
    }

    @Test
    @DisplayName("calculateTotalPrice() throws when reservation not found")
    void calculateTotalPrice_notFound() {
        given(reservationClient.getReservationById(reservationId)).willReturn(null);

        assertThatThrownBy(() -> billService.calculateTotalPrice(reservationId))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessage("Reservation with ID 10 not found.");
    }

    /* ---------- createBill ---------- */

    @Test
    @DisplayName("createBill() builds and saves a Bill")
    void createBill_success() throws ReservationNotFoundException {
        given(reservationClient.getReservationById(reservationId)).willReturn(reservationDTO);
        given(billRepository.findTopByOrderByIdDesc()).willReturn(Optional.empty()); // last id = 0
        given(billRepository.save(any(Bill.class))).willAnswer(inv -> inv.getArgument(0));

        Bill bill = billService.createBill(reservationId);

        assertThat(bill.getBillNo()).isEqualTo("BILL001");
        assertThat(bill.getReservationId()).isEqualTo(reservationId);
        assertThat(bill.getBaseAmount()).isEqualTo(baseAmount);
        assertThat(bill.getTaxAmount()).isEqualTo(baseAmount * 0.18);
        assertThat(bill.getTotalAmount()).isEqualTo(baseAmount * 1.18);
        assertThat(bill.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("createBill() throws when reservation not found")
    void createBill_notFound() {
        given(reservationClient.getReservationById(reservationId)).willReturn(null);

        assertThatThrownBy(() -> billService.createBill(reservationId))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessage("Reservation with ID 10 not found.");
    }

    /* ---------- getAllBills ---------- */

    @Test
    @DisplayName("getAllBills() delegates to repository")
    void getAllBills() {
        List<Bill> expected = List.of(new Bill());
        given(billRepository.findAll()).willReturn(expected);

        List<Bill> actual = billService.getAllBills();

        assertThat(actual).isEqualTo(expected);
    }

    /* ---------- getLastBillId ---------- */

    @Test
    @DisplayName("getLastBillId() returns 0 when table empty")
    void getLastBillId_empty() {
        given(billRepository.findTopByOrderByIdDesc()).willReturn(Optional.empty());

        assertThat(billService.getLastBillId()).isZero();
    }

    @Test
    @DisplayName("getLastBillId() returns highest id")
    void getLastBillId_populated() {
        Bill bill = new Bill();
        bill.setId(42L);
        given(billRepository.findTopByOrderByIdDesc()).willReturn(Optional.of(bill));

        assertThat(billService.getLastBillId()).isEqualTo(42L);
    }
}