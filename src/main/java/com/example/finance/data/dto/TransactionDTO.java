package com.example.finance.data.dto;

import com.example.finance.data.entity.Expense;
import com.example.finance.data.entity.Income;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private List<Income> income = new ArrayList<>();
    private List<Expense> expense = new ArrayList<>();
}
