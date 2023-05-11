package com.example.auth.service;

import com.example.auth.data.entity.*;
import com.example.auth.exception.DataNotFound;
import com.example.auth.exception.InsufficientFundsException;
import com.example.auth.repository.*;
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
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    public CardAccount save(CardAccount cardAccount) {
        User user = getCurrentUser();
        cardAccount.setUser(user);
        return cardAccountRepository.save(cardAccount);
    }

    public List<CardAccount> getAll() {
        User user = getCurrentUser();
        return cardAccountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Card accounts not found"));
    }

    public CardAccount getById(Long cardId) {
        User user = getCurrentUser();
        return cardAccountRepository.findByIdAndUserId(cardId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Card account not found"));
    }

    public CardAccount switchBalance(Long carId, BigDecimal balance) {
        CardAccount cardAccount = getById(carId);
        cardAccount.setBalance(balance);
        return cardAccountRepository.save(cardAccount);
    }

    @Transactional
    public CardAccount addMoney(Income income, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("category not found"));
        CardAccount cardAccount = getById(income.getCardId());
        cardAccount.addMoney(income.getAmount());
        income.setUser(cardAccount.getUser());
        income.setCategory(category);
        eventPublisher.publishEvent(income);
        return cardAccountRepository.save(cardAccount);
    }

    //    @Transactional
//    public CardAccount subtractMoney(Expense expense, Long categoryId) {
//        Category category = categoryRepository.findById(categoryId)
//                .orElseThrow(() -> new EntityNotFoundException("category not found"));
//        CardAccount cardAccount = getById(expense.getCardId());
//        cardAccount.subtractMoney(expense.getAmount());
//        expense.setUser(cardAccount.getUser());
//        expense.setCategory(category);
//        expenseRepository.save(expense);
////        eventPublisher.publishEvent(expense);
//        return cardAccountRepository.save(cardAccount);
//    }
    @Transactional
    public CardAccount subtractMoney(Expense expense, Long categoryId) {
        CardAccount cardAccount = getById(expense.getCardId());

        if (cardAccount.getBalance().compareTo(expense.getAmount()) < 0) {
            throw new InsufficientFundsException("not money");
        }
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DataNotFound("category not found"));

        cardAccount.subtractMoney(expense.getAmount());
        expense.setUser(cardAccount.getUser());
        expense.setCategory(category);

        expenseRepository.save(expense);
        cardAccountRepository.save(cardAccount);
        return cardAccount;
    }
}
