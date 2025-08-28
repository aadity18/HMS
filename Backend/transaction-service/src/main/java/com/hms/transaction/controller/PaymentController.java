package com.hms.transaction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hms.transaction.service.PaymentService;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create/{reservationId}")
    public Map<String, Object> handlePayment(@PathVariable Long reservationId) {
        System.out.println("[DEBUG] handlePayment called for reservationId: " + reservationId);
        Map<String, Object> result = paymentService.handlePayment(reservationId);
        System.out.println("[DEBUG] handlePayment result: " + result);
        return result;
    }

    @PostMapping("/payment-success/{paymentId}")
    public Map<String, Object> paymentSuccess(@PathVariable Long paymentId) {
        System.out.println("[DEBUG] paymentSuccess called for paymentId: " + paymentId);
        Map<String, Object> result = paymentService.handlePaymentSuccess(paymentId);
        System.out.println("[DEBUG] paymentSuccess result: " + result);
        return result;
    }

    @PostMapping("/payment-failure/{paymentId}")
    public Map<String, Object> paymentFailure(@PathVariable Long paymentId) {
        System.out.println("[DEBUG] paymentFailure called for paymentId: " + paymentId);
        Map<String, Object> result = paymentService.handlePaymentFailure(paymentId);
        System.out.println("[DEBUG] paymentFailure result: " + result);
        return result;
    }
}
