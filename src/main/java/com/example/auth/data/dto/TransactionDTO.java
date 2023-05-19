package com.example.auth.data.dto;

import com.example.auth.data.entity.Expense;
import com.example.auth.data.entity.Income;
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
