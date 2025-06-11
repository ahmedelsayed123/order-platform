package com.example.demo.model;

import com.example.demo.model.enums.PaymentStatus;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Embeddable
public class PaymentInfo {
    private String cardToken;
    private String paymentGateway;
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}