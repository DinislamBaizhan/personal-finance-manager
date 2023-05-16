package com.example.auth.service;

import com.example.auth.data.entity.Debt;
import com.example.auth.data.entity.Expense;
import com.example.auth.data.entity.User;
import com.example.auth.data.enums.DebtType;
import com.example.auth.repository.DebtRepository;
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
public class LoanService {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final CashAccountService cashAccountService;
    private final CardAccountService cardAccountService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    public Debt save(Debt loan) {
        loan.setDebtType(DebtType.LOAN);
        User user = getCurrentUser();
        loan.setUser(user);

        return debtRepository.save(loan);
    }

    public List<Debt> getAll() {
        User user = getCurrentUser();
        return debtRepository.getDebtsByDebtTypeAndUserId(DebtType.LOAN, user.getId());
    }

    public Debt getById(Long loan) {
        User user = getCurrentUser();
        return debtRepository.findByDebtTypeAndUserIdAndId(DebtType.LOAN, user.getId(), loan)
                .orElseThrow(() -> new EntityNotFoundException("loan not found"));
    }

    public List<Debt> getAllActive() {
        User user = getCurrentUser();
        return debtRepository.findAllByDebtTypeAndUserIdAndActiveIsTrue(DebtType.LOAN, user.getId());
    }

    public List<Debt> getAllNotActive() {
        User user = getCurrentUser();
        return debtRepository.findAllByDebtTypeAndUserIdAndActiveIsFalse(DebtType.LOAN, user.getId());
    }

    @Transactional
    public Debt repay(Expense expense, Long loanId) {
        User user = getCurrentUser();
        Debt loan = getById(loanId);
        if (loan.getIndebtedness().compareTo(expense.getAmount()) < 0) {
            throw new IllegalArgumentException("you can't spend more than you owe");
        } else {
            loan.setUser(user);
            loan.setDebtType(DebtType.LOAN);
            loan.subtractMoney(expense.getAmount());

            switch (expense.getAccountType()) {
                case CARD -> cardAccountService.subtractMoney(expense, 13L);
                case CASH -> cashAccountService.subtractMoney(expense, 13L);
            }
            return debtRepository.save(loan);
        }
    }

    public Debt increaseLoan(Long loanId, BigDecimal amount) {
        Debt loan = getById(loanId);
        loan.addMoney(amount);
        return debtRepository.save(loan);
    }
}

