package com.example.auth.service;

import com.example.auth.repository.ExpenseRepository;
import com.example.auth.repository.IncomeRepository;
import com.example.auth.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

//    public
}
