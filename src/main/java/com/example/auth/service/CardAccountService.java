package com.example.auth.service;

import com.example.auth.data.entity.*;
import com.example.auth.data.enums.AccountType;
import com.example.auth.exception.DataNotFound;
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
public class CardAccountService {
    private final UserRepository userRepository;
    private final CardAccountRepository cardAccountRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CategoryRepository categoryRepository;
    private final CashAccountRepository cashAccountRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    public CardAccount save(CardAccount cardAccount) {
        User user = getCurrentUser();
        cardAccount.setAccountType(AccountType.CARD);
        cardAccount.setUser(user);
        return cardAccountRepository.save(cardAccount);
    }

    public List<CardAccount> getAll() {
        User user = getCurrentUser();
        return cardAccountRepository.findAllByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Card accounts not found"));
    }

    public CardAccount getById(Long accountId) {
        User user = getCurrentUser();
        return cardAccountRepository.findByIdAndUserId(accountId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Card account not found"));
    }

    public CardAccount switchBalance(Long accountId, BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("balance cannot be less than zero");
        } else {
            CardAccount cardAccount = getById(accountId);
            cardAccount.setBalance(balance);
            return cardAccountRepository.save(cardAccount);
        }
    }

    @Transactional
    public CardAccount addMoney(Income income, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow();
        CardAccount cardAccount = getById(income.getAccountId());
        cardAccount.addMoney(income.getAmount());
        income.setUser(cardAccount.getUser());
        income.setCategory(category);
        income.setAccountName(cardAccount.getName());
        income.setAccountType(AccountType.CARD);

        eventPublisher.publishEvent(income);
        return cardAccountRepository.save(cardAccount);
    }

    @Transactional
    public CardAccount subtractMoney(Expense expense, Long categoryId) {
        CardAccount cardAccount = getById(expense.getAccountId());
        BigDecimal balance = cardAccount.getBalance();
        BigDecimal amount = expense.getAmount();

        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("not money");
        } else {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new DataNotFound("category not found"));

            cardAccount.subtractMoney(expense.getAmount());
            expense.setUser(cardAccount.getUser());
            expense.setCategory(category);
            expense.setAccountName(cardAccount.getName());
            expense.setAccountType(AccountType.CARD);

            CardAccount newAccount = cardAccountRepository.save(cardAccount);
            eventPublisher.publishEvent(expense);
            return newAccount;
        }
    }
}
