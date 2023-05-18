package com.example.auth.data.dto;

import com.example.auth.data.entity.Expense;
import com.example.auth.data.entity.Income;
import com.example.auth.data.enums.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private List<Income> income = new ArrayList<>();
    private List<Expense> expense = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IncomeDTO {
        private LocalDateTime dateTime;
        private BigDecimal amount;
        private String description;
        private Long accountId;
        private String accountName;
        private String category;
        private AccountType accountType;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExpenseDTO {
        private LocalDateTime dateTime;
        private BigDecimal amount;
        private String description;
        private Long accountId;
        private String accountName;
        private String category;
        private AccountType accountType;
    }
}
