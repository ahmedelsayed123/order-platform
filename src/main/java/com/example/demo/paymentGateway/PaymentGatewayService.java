package com.example.demo.paymentGateway;

import com.example.demo.model.enums.PaymentStatus;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PaymentGatewayService {

    /**
     * This is a mock payment gateway service
     * Processes the payment based on the provided gateway.
     * If the gateway is "OFFLINE", it returns PaymentStatus.OFFLINE.
     * For other gateways, it simulates a payment process with an 80% chance of success.
     *
     * @param gateway the payment gateway to use
     * @return the status of the payment
     */


    public PaymentStatus processPayment(String gateway) {


        if ("OFFLINE".equalsIgnoreCase(gateway)) {
            return PaymentStatus.OFFLINE;
        }

        // 80% chance of success

        return new Random().nextDouble() > 0.2 ? PaymentStatus.PAID : PaymentStatus.PAYMENT_FAILED;

    }

}