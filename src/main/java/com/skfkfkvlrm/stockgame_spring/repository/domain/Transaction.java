package com.skfkfkvlrm.stockgame_spring.repository.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    @CreationTimestamp
    private LocalDateTime createdDate;
    private Long sellOrderNo;
    private Long buyOrderNo;
}
