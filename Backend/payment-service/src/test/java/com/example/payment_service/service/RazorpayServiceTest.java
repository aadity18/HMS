package com.example.payment_service.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RazorpayServiceTest {

//    @Test
//    @DisplayName("createOrder() builds correct JSON and returns mocked order")
//    void createOrder_buildsCorrectRequest() throws Exception {
//        try (MockedConstruction<RazorpayClient> mockedClient =
//                     Mockito.mockConstruction(RazorpayClient.class,
//                             (mock, context) -> {
//                                 Order mockOrder = mock(Order.class);
//                                 given(mockOrder.get("id")).willReturn("order_123");
//                                 given(mockOrder.get("currency")).willReturn("INR");
//
//                                 // client.orders.create(...)
//                                 given(mock.orders.create(any(JSONObject.class)))
//                                         .willReturn(mockOrder);
//                             })) {
//
//            RazorpayService service = new RazorpayService("dummyKey", "dummySecret");
//            Order order = service.createOrder(2500.0, "INR", "rcpt_1");
//
//            assertThat(order.get("id")).isEqualTo("order_123");
//        }
//    }
}