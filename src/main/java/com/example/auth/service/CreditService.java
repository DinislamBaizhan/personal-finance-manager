package com.example.auth.service;

import com.example.auth.data.entity.Debt;
import com.example.auth.data.entity.Expense;
import com.example.auth.data.entity.User;
import com.example.auth.data.enums.DebtType;
import com.example.auth.repository.DebtRepository;
import com.example.auth.repository.ExpenseRepository;
import com.example.auth.repository.IncomeRepository;
import com.example.auth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditService {
    private final UserRepository userRepository;
    private final DebtRepository debtRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final CardAccountService cardAccountService;
    private final CashAccountService cashAccountService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    public Debt save(Debt credit) {
        credit.setDebtType(DebtType.CREDIT);
        User user = getCurrentUser();
        credit.setUser(user);

        return debtRepository.save(credit);
    }

    public List<Debt> getAll() {
        User user = getCurrentUser();
        return debtRepository.getDebtsByDebtTypeAndUserId(DebtType.CREDIT, user.getId());
    }

    public Debt getById(Long creditId) {
        User user = getCurrentUser();
        return debtRepository.findByDebtTypeAndUserIdAndId(DebtType.CREDIT, user.getId(), creditId)
                .orElseThrow(() -> new EntityNotFoundException("credit not found"));
    }

    public List<Debt> getAllActive() {
        User user = getCurrentUser();
        return debtRepository.findAllByDebtTypeAndUserIdAndActiveIsTrue(DebtType.CREDIT, user.getId());
    }

    public List<Debt> getAllNotActive() {
        User user = getCurrentUser();
        return debtRepository.findAllByDebtTypeAndUserIdAndActiveIsFalse(DebtType.CREDIT, user.getId());
    }

    @Transactional
    public Debt repay(Expense expense, Long creditId) {
        User user = getCurrentUser();
        Debt credit = getById(creditId);

        if (credit.getIndebtedness().compareTo(expense.getAmount()) < 0) {
            throw new IllegalArgumentException("you can't spend more than you owe");
        } else {
            credit.setUser(user);
            credit.setDebtType(DebtType.CREDIT);
            credit.subtractMoney(expense.getAmount());

            switch (expense.getAccountType()) {
                case CARD -> cardAccountService.subtractMoney(expense, 13L); //Категория кредит
                case CASH -> cashAccountService.subtractMoney(expense, 13L); //Категория кредит
            }
            return debtRepository.save(credit);
        }
    }

    public Debt increaseCredit(Long creditId, BigDecimal amount) {
        Debt credit = getById(creditId);
        credit.addMoney(amount);
        return debtRepository.save(credit);
    }

    public boolean setActivity(Long creditId, boolean status) {
        Debt debt = getById(creditId);
        debt.setActive(status);
        debtRepository.save(debt);
        return debt.isActive();
    }
}
