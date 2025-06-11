package com.example.demo.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class BuyerInfo {

    private String buyerEmail;
    private String seatLetter;
    private int seatNumber;
}