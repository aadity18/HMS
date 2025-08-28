package com.example.payment_service.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    private RazorpayClient client;

    public RazorpayService(@Value("${razorpay.key_id}") String keyId,
                           @Value("${razorpay.key_secret}") String keySecret) throws Exception {
        this.client = new RazorpayClient(keyId, keySecret);
    }

    public Order createOrder(Double amount, String currency, String receipt) throws Exception {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(amount * 100)); // amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", receipt);
        orderRequest.put("payment_capture", 1); // auto-capture

        return client.orders.create(orderRequest);
    }
}
