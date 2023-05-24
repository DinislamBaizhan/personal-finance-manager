package com.example.finance.service;

import com.example.finance.data.entity.CashAccount;
import com.example.finance.data.entity.Expense;
import com.example.finance.data.entity.Income;
import com.example.finance.data.entity.User;
import com.example.finance.repository.CashAccountRepository;
import com.example.finance.repository.UserRepository;
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
    private final ApplicationEventPublisher eventPublisher;
    private final AccountService<CashAccount> accountService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
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
        CashAccount cashAccount = getById(income.getAccountId());
        CashAccount cashAccount1 = accountService.addMoney(cashAccount, income, categoryId);
        eventPublisher.publishEvent(income);
        return cashRepository.save(cashAccount1);
    }

    @Transactional
    public CashAccount subtractMoney(Expense expense, Long categoryId) {
        CashAccount cashAccount = getById(expense.getAccountId());
        CashAccount cashAccount1 = accountService.subtractMoney(cashAccount, expense, categoryId);
        eventPublisher.publishEvent(expense);
        return cashRepository.save(cashAccount1);
    }

    public BigDecimal setLimit(Long cashId, BigDecimal limit) {
        CashAccount cashAccount = getById(cashId);
        cashAccount.setMoneyLimit(limit);
        return cashRepository.save(cashAccount).getMoneyLimit();
    }

    public void delete(Long cashId) {
        CashAccount cashAccount = getById(cashId);
        try {
            cashRepository.delete(cashAccount);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
