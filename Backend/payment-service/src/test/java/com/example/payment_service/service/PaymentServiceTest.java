package com.example.payment_service.service;

import com.example.payment_service.dto.BillDTO;
import com.example.payment_service.model.Payment;
import com.example.payment_service.repository.PaymentRepository;
import com.razorpay.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private RazorpayService razorpayService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BillingClient billingClient;
    @Mock
    private ReservationServiceClient reservationServiceClient;

    @InjectMocks
    private PaymentService paymentService;

    private final Long reservationId = 10L;
    private final Double amount = 2500.0;

    private Payment savedPayment;

    @BeforeEach
    void setUp() {
        savedPayment = new Payment();
        savedPayment.setId(99L);
        savedPayment.setReservationId(reservationId);
        savedPayment.setAmountPaid(amount);
        savedPayment.setPaymentSuccess(false);
    }

    /* ---------- handlePayment (happy) ---------- */

    @Test
    @DisplayName("handlePayment() creates order and returns details")
    void handlePayment_success() throws Exception {
        Order razorOrder = Mockito.mock(Order.class);
        given(razorOrder.get("id")).willReturn("order_xyz");
        given(razorOrder.get("currency")).willReturn("INR");

        // use eq() for every literal
        given(razorpayService.createOrder(eq(amount), eq("INR"), anyString()))
                .willReturn(razorOrder);

        given(billingClient.getTotalPrice(reservationId)).willReturn(amount);
        given(paymentRepository.save(any(Payment.class))).willReturn(savedPayment);

        Map<String, Object> result = paymentService.handlePayment(reservationId);

        assertThat(result)
                .containsEntry("orderId", "order_xyz")
                .containsEntry("amount", amount)
                .containsEntry("paymentId", 99L);
    }

    @Test
    @DisplayName("handlePayment() throws when billing client fails")
    void handlePayment_billingFailure() {
        given(billingClient.getTotalPrice(reservationId))
                .willThrow(new RuntimeException("billing down"));

        assertThatThrownBy(() -> paymentService.handlePayment(reservationId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to fetch total price");
    }

    /* ---------- handlePaymentSuccess ---------- */

    @Test
    @DisplayName("handlePaymentSuccess() marks ok and updates reservation")
    void handlePaymentSuccess_ok() {
        given(paymentRepository.findById(99L)).willReturn(Optional.of(savedPayment));
        given(paymentRepository.save(savedPayment)).willReturn(savedPayment);

        Map<String, Object> res = paymentService.handlePaymentSuccess(99L);

        assertThat(res).containsEntry("message", "Payment successful");
        verify(reservationServiceClient).updateReservationStatus(reservationId, true);
    }

    @Test
    @DisplayName("handlePaymentSuccess() throws when payment missing")
    void handlePaymentSuccess_notFound() {
        given(paymentRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.handlePaymentSuccess(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Payment not found");
    }

    /* ---------- handlePaymentFailure ---------- */

    @Test
    @DisplayName("handlePaymentFailure() marks failed and cancels reservation")
    void handlePaymentFailure_ok() {
        given(paymentRepository.findById(99L)).willReturn(Optional.of(savedPayment));
        given(paymentRepository.save(savedPayment)).willReturn(savedPayment);

        Map<String, Object> res = paymentService.handlePaymentFailure(99L);

        assertThat(res).containsEntry("message", "Payment failed, reservation cancelled");
        verify(reservationServiceClient).updateReservationStatus(reservationId, false);
    }

    @Test
    @DisplayName("handlePaymentFailure() throws when payment missing")
    void handlePaymentFailure_notFound() {
        given(paymentRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.handlePaymentFailure(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Payment not found");
    }
}