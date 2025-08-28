package com.hms.transaction.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.transaction.dto.ReservationDTO;
import com.hms.transaction.exception.ReservationNotFoundException;
import com.hms.transaction.model.Bill;
import com.hms.transaction.repository.BillRepository;

@Service
public class BillService {

    @Autowired 
    private BillRepository billRepository;
    @Autowired 
    private ReservationClient reservationClient;

    public Double calculateTotalPrice(Long reservationId) throws ReservationNotFoundException {
        ReservationDTO dto = reservationClient.getReservationById(reservationId);
        if (dto == null) {
            throw new ReservationNotFoundException("Reservation with ID " + reservationId + " not found.");
        }
        double base = dto.getTotalPrice();
        double tax = base * 0.18;
        return base + tax;
    }
    
    public Long getLastBillId() {
        return billRepository.findTopByOrderByIdDesc()
                .map(Bill::getId)
                .orElse(0L); // Default 0 if no reservations exist
    }

    public Bill createBill(Long reservationId) throws ReservationNotFoundException {
        ReservationDTO dto = reservationClient.getReservationById(reservationId);
        if (dto == null) {
            throw new ReservationNotFoundException("Reservation with ID " + reservationId + " not found.");
        }
        double tax = dto.getTotalPrice() * 0.18;

        Bill bill = new Bill();
        Long nextId = getLastBillId() + 1;
        String billCode = "BILL" + String.format("%03d", nextId);
        bill.setBillNo(billCode);
        bill.setReservationId(reservationId);
        bill.setReservationCode(dto.getCode());
        bill.setGuestCode(dto.getGuestCode());
        bill.setRoomCode(dto.getRoomCode());
        bill.setBaseAmount(dto.getTotalPrice());
        bill.setTaxAmount(tax);
        bill.setTotalAmount(dto.getTotalPrice() + tax);
        bill.setDate(LocalDate.now());

        return billRepository.save(bill);
    }

	public List<Bill> getAllBills() {
		return billRepository.findAll();
	}
}