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
        System.out.println("[DEBUG] handlePayment called for reservationId: " + reservationId);

        Double total;
        try {
            total = billingClient.getTotalPrice(reservationId);
            System.out.println("[DEBUG] Total price fetched: " + total);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to fetch total price: " + e.getMessage());
            throw new RuntimeException("Failed to fetch total price: " + e.getMessage());
        }

        // Step 1: Create Razorpay order
        String receiptId = "txn_" + System.currentTimeMillis();
        Order order;
        try {
            order = razorpayService.createOrder(total, "INR", receiptId);
            System.out.println("[DEBUG] Razorpay order created: " + order);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to create Razorpay order: " + e.getMessage());
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
            System.out.println("[DEBUG] Payment saved in DB: " + saved);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to save payment: " + e.getMessage());
            throw new RuntimeException("Failed to save payment: " + e.getMessage());
        }

        // Step 3: Generate bill
        BillDTO bill;
        try {
            Map<String, Long> map = new HashMap<>();
            map.put("reservationId", reservationId);
            bill = billingClient.generateBill(map);
            System.out.println("[DEBUG] Bill generated: " + bill);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to generate bill: " + e.getMessage());
           // throw new RuntimeException("Failed to generate bill: " + e.getMessage());
        }

        return Map.of(
                "message", "Razorpay order created. Proceed to pay using Razorpay UI",
                "orderId", order.get("id"),
                "amount", total,
                "currency", order.get("currency"),
                "paymentId", saved.getId()
               // "billNo", bill.getBillNo()
        );
    }

    // Handle payment success callback
    public Map<String, Object> handlePaymentSuccess(Long paymentId) {
        System.out.println("[DEBUG] handlePaymentSuccess called for paymentId: " + paymentId);

        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> {
            System.out.println("[ERROR] Payment not found for id: " + paymentId);
            return new RuntimeException("Payment not found");
        });
        System.out.println("[DEBUG] Payment found: " + payment);

        payment.setPaymentSuccess(true);
        paymentRepository.save(payment);
        System.out.println("[DEBUG] Payment marked as success in DB");

        try {
            reservationServiceClient.updateReservationStatus(payment.getReservationId(), true);
            System.out.println("[DEBUG] Reservation status updated to BOOKED for reservationId: " + payment.getReservationId());
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to update reservation status: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        
        

        return Map.of("message", "Payment successful", "paymentId", paymentId);
    }

    // Handle payment failure callback
    public Map<String, Object> handlePaymentFailure(Long paymentId) {
        System.out.println("[DEBUG] handlePaymentFailure called for paymentId: " + paymentId);

        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> {
            System.out.println("[ERROR] Payment not found for id: " + paymentId);
            return new RuntimeException("Payment not found");
        });
        System.out.println("[DEBUG] Payment found: " + payment);

        payment.setPaymentSuccess(false);
        paymentRepository.save(payment);
        System.out.println("[DEBUG] Payment marked as failed in DB");

        try {
            reservationServiceClient.updateReservationStatus(payment.getReservationId(), false);
            System.out.println("[DEBUG] Reservation status updated to CANCELLED for reservationId: " + payment.getReservationId());
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to update reservation status: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return Map.of("message", "Payment failed, reservation cancelled", "paymentId", paymentId);
    }
}
