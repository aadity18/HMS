package com.hms.transaction.service;

import com.hms.transaction.dto.BillDTO;
import com.hms.transaction.model.Payment;
import com.hms.transaction.repository.PaymentRepository;
import com.razorpay.Order;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private RazorpayService razorpayService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BillingClient billingClient;
    @Mock
    private ReservationServiceClient reservationServiceClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandlePayment_Success() throws Exception {
        Long reservationId = 1L;
        Double total = 1000.0;

        when(billingClient.getTotalPrice(reservationId)).thenReturn(total);

        // Mock Razorpay order
        JSONObject orderJson = new JSONObject();
        orderJson.put("id", "order_123");
        orderJson.put("currency", "INR");
        Order order = new Order(orderJson);
        when(razorpayService.createOrder(eq(total), eq("INR"), anyString())).thenReturn(order);

        // Mock Payment save
        Payment savedPayment = new Payment();
        savedPayment.setId(10L);
        savedPayment.setReservationId(reservationId);
        savedPayment.setAmountPaid(total);
        savedPayment.setPaymentTime(LocalDateTime.now());
        savedPayment.setRazorpayOrderId("order_123");
        savedPayment.setPaymentSuccess(false);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Mock bill
        BillDTO bill = new BillDTO();
        bill.setBillNo("BILL001");
        when(billingClient.generateBill(anyMap())).thenReturn(bill);

        Map<String, Object> result = paymentService.handlePayment(reservationId);

        assertEquals("Razorpay order created. Proceed to pay using Razorpay UI", result.get("message"));
        assertEquals("order_123", result.get("orderId"));
        assertEquals(total, result.get("amount"));
        assertEquals("INR", result.get("currency"));
        assertEquals(10L, result.get("paymentId"));

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testHandlePayment_FailsOnBillingClient() {
        Long reservationId = 1L;
        when(billingClient.getTotalPrice(reservationId)).thenThrow(new RuntimeException("Billing error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> paymentService.handlePayment(reservationId));
        assertTrue(ex.getMessage().contains("Failed to fetch total price"));
    }

    @Test
    void testHandlePaymentSuccess() {
        Long paymentId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setReservationId(100L);
        payment.setPaymentSuccess(false);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Map<String, Object> result = paymentService.handlePaymentSuccess(paymentId);

        assertEquals("Payment successful", result.get("message"));
        assertEquals(paymentId, result.get("paymentId"));
        assertTrue(payment.isPaymentSuccess());

        verify(reservationServiceClient, times(1))
                .updateReservationStatus(eq(100L), eq(true));
    }

    @Test
    void testHandlePaymentFailure() {
        Long paymentId = 2L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setReservationId(200L);
        payment.setPaymentSuccess(true);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Map<String, Object> result = paymentService.handlePaymentFailure(paymentId);

        assertEquals("Payment failed, reservation cancelled", result.get("message"));
        assertEquals(paymentId, result.get("paymentId"));
        assertFalse(payment.isPaymentSuccess());

        verify(reservationServiceClient, times(1))
                .updateReservationStatus(eq(200L), eq(false));
    }

    @Test
    void testHandlePaymentSuccess_NotFound() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.handlePaymentSuccess(999L));
        assertEquals("Payment not found", ex.getMessage());
    }

    @Test
    void testHandlePaymentFailure_NotFound() {
        when(paymentRepository.findById(888L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.handlePaymentFailure(888L));
        assertEquals("Payment not found", ex.getMessage());
    }
}
