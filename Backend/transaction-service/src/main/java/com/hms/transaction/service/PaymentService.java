package com.hms.transaction.service;

import com.hms.transaction.dto.BillDTO;
import com.hms.transaction.model.Payment;
import com.hms.transaction.repository.PaymentRepository;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Autowired
    private RazorpayService razorpayService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BillingClient billingClient;
    @Autowired
    private ReservationServiceClient reservationServiceClient;

    // Handle payment request and create Razorpay order
    public Map<String, Object> handlePayment(Long reservationId) {
        Double total;
        try {
            total = billingClient.getTotalPrice(reservationId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch total price: " + e.getMessage());
        }

        // Step 1: Create Razorpay order
        String receiptId = "txn_" + System.currentTimeMillis();
        Order order;
        try {
            order = razorpayService.createOrder(total, "INR", receiptId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage());
        }

        // Step 2: Save payment data in DB
        Payment payment = new Payment();
        payment.setReservationId(reservationId);
        payment.setAmountPaid(total);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setRazorpayOrderId(order.get("id").toString());
        payment.setPaymentSuccess(false);

        Payment saved;
        try {
            saved = paymentRepository.save(payment);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save payment: " + e.getMessage());
        }

        // Step 3: Generate bill
        try {
            Map<String, Long> map = new HashMap<>();
            map.put("reservationId", reservationId);
            BillDTO bill = billingClient.generateBill(map);
        } catch (Exception e) {
            // Bill generation failed but don’t stop payment flow
        }

        return Map.of(
                "message", "Razorpay order created. Proceed to pay using Razorpay UI",
                "orderId", order.get("id"),
                "amount", total,
                "currency", order.get("currency"),
                "paymentId", saved.getId()
        );
    }

    // Handle payment success callback
    public Map<String, Object> handlePaymentSuccess(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() ->
                new RuntimeException("Payment not found"));

        payment.setPaymentSuccess(true);
        paymentRepository.save(payment);

        try {
            reservationServiceClient.updateReservationStatus(payment.getReservationId(), true);
        } catch (Exception e) {
            throw e;
        }

        return Map.of("message", "Payment successful", "paymentId", paymentId);
    }

    // Handle payment failure callback
    public Map<String, Object> handlePaymentFailure(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() ->
                new RuntimeException("Payment not found"));

        payment.setPaymentSuccess(false);
        paymentRepository.save(payment);

        try {
            reservationServiceClient.updateReservationStatus(payment.getReservationId(), false);
        } catch (Exception e) {
            throw e;
        }

        return Map.of("message", "Payment failed, reservation cancelled", "paymentId", paymentId);
    }
}
