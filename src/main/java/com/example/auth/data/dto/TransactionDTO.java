package com.example.auth.data.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDTO {
    private Long cardId;
    private Long categoryId;
    private String description;
    private BigDecimal amount;
    private Long userId;
}
