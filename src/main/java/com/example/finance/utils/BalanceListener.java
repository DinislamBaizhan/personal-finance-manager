package com.example.finance.utils;

import com.example.finance.data.entity.Expense;
import com.example.finance.data.entity.Income;
import com.example.finance.repository.ExpenseRepository;
import com.example.finance.repository.IncomeRepository;
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
