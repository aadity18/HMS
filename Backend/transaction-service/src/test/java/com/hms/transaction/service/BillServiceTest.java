package com.hms.transaction.service;

import com.hms.transaction.dto.ReservationDTO;
import com.hms.transaction.exception.ReservationNotFoundException;
import com.hms.transaction.model.Bill;
import com.hms.transaction.repository.BillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BillServiceTest {

    @InjectMocks
    private BillService billService;

    @Mock
    private BillRepository billRepository;
    @Mock
    private ReservationClient reservationClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateTotalPrice_Success() throws Exception {
        ReservationDTO dto = new ReservationDTO();
        dto.setTotalPrice(1000.0);

        when(reservationClient.getReservationById(1L)).thenReturn(dto);

        Double total = billService.calculateTotalPrice(1L);

        assertEquals(1180.0, total); // 1000 + 18% tax
    }

    @Test
    void testCalculateTotalPrice_NotFound() {
        when(reservationClient.getReservationById(1L)).thenReturn(null);

        assertThrows(ReservationNotFoundException.class,
                () -> billService.calculateTotalPrice(1L));
    }

    @Test
    void testGetLastBillId_ReturnsExisting() {
        Bill bill = new Bill();
        bill.setId(5L);

        when(billRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(bill));

        Long lastId = billService.getLastBillId();

        assertEquals(5L, lastId);
    }

    @Test
    void testGetLastBillId_ReturnsZeroIfNone() {
        when(billRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());

        Long lastId = billService.getLastBillId();

        assertEquals(0L, lastId);
    }

    @Test
    void testCreateBill_Success() throws Exception {
        ReservationDTO dto = new ReservationDTO();
        dto.setCode("RES001");
        dto.setGuestCode("GUEST01");
        dto.setRoomCode("ROOM01");
        dto.setTotalPrice(2000.0);

        when(reservationClient.getReservationById(1L)).thenReturn(dto);
        when(billRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(new Bill() {{ setId(5L); }}));

        Bill bill = new Bill();
        bill.setId(6L);
        bill.setBillNo("BILL006");
        bill.setReservationId(1L);
        bill.setReservationCode("RES001");
        bill.setGuestCode("GUEST01");
        bill.setRoomCode("ROOM01");
        bill.setBaseAmount(2000.0);
        bill.setTaxAmount(360.0);
        bill.setTotalAmount(2360.0);
        bill.setDate(LocalDate.now());

        when(billRepository.save(any(Bill.class))).thenReturn(bill);

        Bill created = billService.createBill(1L);

        assertEquals("BILL006", created.getBillNo());
        assertEquals(2360.0, created.getTotalAmount());
        assertEquals("RES001", created.getReservationCode());
        verify(billRepository, times(1)).save(any(Bill.class));
    }

    @Test
    void testCreateBill_NotFound() {
        when(reservationClient.getReservationById(1L)).thenReturn(null);

        assertThrows(ReservationNotFoundException.class,
                () -> billService.createBill(1L));
    }

    @Test
    void testGetAllBills() {
        Bill bill1 = new Bill();
        bill1.setId(1L);
        Bill bill2 = new Bill();
        bill2.setId(2L);

        when(billRepository.findAll()).thenReturn(List.of(bill1, bill2));

        List<Bill> bills = billService.getAllBills();

        assertEquals(2, bills.size());
    }
}
