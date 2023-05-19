package com.example.auth.service;

import com.example.auth.data.entity.CardAccount;
import com.example.auth.data.entity.CashAccount;
import com.example.auth.data.entity.Expense;
import com.example.auth.data.entity.Income;
import com.example.auth.data.enums.AccountType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final CardAccountService cardAccountService;
    private final CashAccountService cashAccountService;

    @Transactional
    public CardAccount transferFromCardAccount(Long fromId, Long toId, AccountType accountType, BigDecimal amount) {
        Expense expense = createExpense(fromId, amount);
        Income income = createIncome(toId, amount);

        // категория перевод
        if (accountType == AccountType.CARD) {
            cardAccountService.addMoney(income, 5L); // категория перевод
        } else {
            cashAccountService.addMoney(income, 5L); // категория перевод
        }
        return cardAccountService.subtractMoney(expense, 5L); // категория перевод
    }

    @Transactional
    public CashAccount transferFromCashAccount(Long fromId, Long toId, AccountType accountType, BigDecimal amount) {
        Expense expense = createExpense(fromId, amount);
        Income income = createIncome(toId, amount);

        // категория перевод
        if (accountType == AccountType.CARD) {
            cardAccountService.addMoney(income, 5L); // категория перевод
        } else {
            cashAccountService.addMoney(income, 5L); // категория перевод
        }
        return cashAccountService.subtractMoney(expense, 5L); // категория перевод
    }

    private Expense createExpense(Long accountId, BigDecimal amount) {
        Expense expense = new Expense();
        expense.setAccountId(accountId);
        expense.setAmount(amount);
        expense.setDescription("transfer between accounts");
        return expense;
    }

    private Income createIncome(Long accountId, BigDecimal amount) {
        Income income = new Income();
        income.setAccountId(accountId);
        income.setAmount(amount);
        income.setDescription("transfer between accounts");
        return income;
    }
}
