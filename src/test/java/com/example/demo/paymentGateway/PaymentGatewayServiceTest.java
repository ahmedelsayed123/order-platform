package com.example.demo.paymentGateway;

import com.example.demo.model.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PaymentGatewayServiceTest {

    private PaymentGatewayService paymentGatewayService;

    @BeforeEach
    void setUp() {
        paymentGatewayService = new PaymentGatewayService();
    }

    @Test
    void testProcessPayment_OfflineGateway() {
        PaymentStatus result = paymentGatewayService.processPayment("OFFLINE");
        assertEquals(PaymentStatus.OFFLINE, result);
    }

    @Test
    void testProcessPayment_OnlineGateway() {
        PaymentStatus result = paymentGatewayService.processPayment("ONLINE");
        assertNotEquals(PaymentStatus.OFFLINE, result);
    }


}