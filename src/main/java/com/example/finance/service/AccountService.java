package com.example.finance.service;

import com.example.finance.data.base.Account;
import com.example.finance.data.entity.Category;
import com.example.finance.data.entity.Expense;
import com.example.finance.data.entity.Income;
import com.example.finance.data.enums.AccountType;
import com.example.finance.data.enums.TransactionType;
import com.example.finance.exception.InsufficientFundsException;
import com.example.finance.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AccountService<T extends Account> {
    private final CategoryRepository categoryRepository;

    public T subtractMoney(T account, Expense expense, Long categoryId) {
        BigDecimal balance = account.getBalance();
        BigDecimal amount = expense.getAmount();

        if (limitExceeded(account.getMoneyLimit(), amount)) {
            throw new IllegalArgumentException("risk of exceeding the spending limit, you can only spend : "
                    + account.getMoneyLimit());
        } else {
            if (balance.compareTo(amount) < 0) {
                throw new InsufficientFundsException("not money");
            } else {
                Category category = getCategoryById(categoryId);

                account.subtractMoney(expense.getAmount());
                expense.setUser(account.getUser());
                expense.setCategory(category);
                expense.setAccountName(account.getName());
                expense.setAccountType(AccountType.CARD);
                expense.setTransactionType(TransactionType.EXPENSE);
                expense.setCreatedAt(setDateTimeFormat());

                if (account.getMoneyLimit() == null) {
                    return account;
                } else {
                    BigDecimal newLimit = account.getMoneyLimit().subtract(amount);
                    account.setMoneyLimit(newLimit);
                    return account;
                }
            }
        }
    }

    public T addMoney(T account, Income income, Long categoryId) {

        Category category = getCategoryById(categoryId);
        account.addMoney(income.getAmount());
        income.setUser(account.getUser());
        income.setCategory(category);
        income.setAccountName(account.getName());
        income.setAccountType(AccountType.CASH);
        income.setTransactionType(TransactionType.INCOME);
        income.setCreatedAt(setDateTimeFormat());
        return account;
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("category not found"));
    }

    public boolean limitExceeded(BigDecimal limit, BigDecimal expense) {
        if (limit == null || (limit.compareTo(expense) >= 0)) {
            return false;
        } else {
            return true;
        }
    }


    private LocalDateTime setDateTimeFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        return LocalDateTime.parse(dateTime.format(formatter), formatter);
    }

}
