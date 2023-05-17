package com.example.auth.utils;

import com.example.auth.data.entity.Expense;
import com.example.auth.data.entity.Income;
import com.example.auth.repository.ExpenseRepository;
import com.example.auth.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BalanceListener {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChangeBalance(Income income) {
        try {
            incomeRepository.save(income);
        } catch (DataAccessException e) {
            throw new RuntimeException("dont save income " + e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChangeBalance(Expense expense) {
        try {
            expenseRepository.save(expense);
        } catch (DataAccessException e) {
            throw new RuntimeException("dont save expense " + e);
        }
    }
}
