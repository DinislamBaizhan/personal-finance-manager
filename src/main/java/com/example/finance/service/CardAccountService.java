package com.example.finance.service;

import com.example.finance.data.entity.CardAccount;
import com.example.finance.data.entity.Expense;
import com.example.finance.data.entity.Income;
import com.example.finance.data.entity.User;
import com.example.finance.data.enums.AccountType;
import com.example.finance.repository.CardAccountRepository;
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
public class CardAccountService {
    private final UserRepository userRepository;
    private final CardAccountRepository cardAccountRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AccountService<CardAccount> accountService;

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
        CardAccount cardAccount = getById(income.getAccountId());
        CardAccount cardAccount1 = accountService.addMoney(cardAccount, income, categoryId);

        eventPublisher.publishEvent(income);
        return cardAccountRepository.save(cardAccount1);
    }

    @Transactional
    public CardAccount subtractMoney(Expense expense, Long categoryId) {
        CardAccount cardAccount = getById(expense.getAccountId());
        CardAccount cardAccount1 = accountService.subtractMoney(cardAccount, expense, categoryId);
        eventPublisher.publishEvent(expense);
        return cardAccountRepository.save(cardAccount1);
    }

    public BigDecimal setLimit(Long carId, BigDecimal limit) {
        CardAccount cardAccount = getById(carId);
        cardAccount.setMoneyLimit(limit);
        return cardAccountRepository.save(cardAccount).getMoneyLimit();
    }

    public void delete(Long cardId) {
        CardAccount cardAccount = getById(cardId);
        try {
            cardAccountRepository.delete(cardAccount);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }
}
