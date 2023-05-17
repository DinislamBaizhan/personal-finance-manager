package com.example.auth.service;

import com.example.auth.data.entity.*;
import com.example.auth.exception.InsufficientFundsException;
import com.example.auth.repository.CardAccountRepository;
import com.example.auth.repository.CashAccountRepository;
import com.example.auth.repository.CategoryRepository;
import com.example.auth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CashAccountService {
    private final UserRepository userRepository;
    private final CashAccountRepository cashRepository;
    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CardAccountRepository cardAccountRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow();
    }

    public CashAccount save(CashAccount cashAccount) {
        User user = getCurrentUser();
        cashAccount.setUser(user);
        return cashRepository.save(cashAccount);
    }

    public List<CashAccount> getAll() {
        User user = getCurrentUser();
        return cashRepository.findAllByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("cash accounts not found"));
    }

    public CashAccount getById(Long cashId) {
        User user = getCurrentUser();
        return cashRepository.findByIdAndUserId(cashId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("cash account not found"));
    }

    public CashAccount switchBalance(Long carId, BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("balance cannot be less than zero");
        } else {
            CashAccount cardAccount = getById(carId);
            cardAccount.setBalance(balance);
            return cashRepository.save(cardAccount);
        }
    }

    @Transactional
    public CashAccount addMoney(Income income, Long categoryId) {

        Category category = getCategory(categoryId);
        CashAccount cashAccount = getById(income.getAccountId());
        cashAccount.addMoney(income.getAmount());
        income.setUser(cashAccount.getUser());
        income.setCategory(category);

        CashAccount newAccount = cashRepository.save(cashAccount);
        eventPublisher.publishEvent(income);
        return newAccount;
    }

    @Transactional
    public CashAccount subtractMoney(Expense expense, Long categoryId) {

        CashAccount cashAccount = getById(expense.getAccountId());
        BigDecimal balance = cashAccount.getBalance();
        BigDecimal amount = expense.getAmount();

        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("not money");
        } else {
            Category category = getCategory(categoryId);

            cashAccount.subtractMoney(expense.getAmount());
            expense.setUser(cashAccount.getUser());
            expense.setCategory(category);

            CashAccount newAccount = cashRepository.save(cashAccount);
            eventPublisher.publishEvent(expense);
            return newAccount;
        }
    }
}
