package com.example.billing_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.billing_service.exception.ReservationNotFoundException;
import com.example.billing_service.model.Bill;
import com.example.billing_service.service.BillService;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping("/price/{reservationId}")
    public Double getTotalPrice(@PathVariable Long reservationId) throws ReservationNotFoundException {
        return billService.calculateTotalPrice(reservationId);
    }

    @PostMapping("/generate")
    public ResponseEntity<Bill> generateBill(@RequestBody Map<String, Long> req) throws ReservationNotFoundException {
        Long reservationId = req.get("reservationId");
        return ResponseEntity.ok(billService.createBill(reservationId));
    }
    
    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills(){
    	return ResponseEntity.ok(billService.getAllBills());
    }
}