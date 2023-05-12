package com.example.auth.service;

import com.example.auth.data.entity.Debt;
import com.example.auth.data.entity.User;
import com.example.auth.data.enums.DebtType;
import com.example.auth.repository.DebtRepository;
import com.example.auth.repository.ExpenseRepository;
import com.example.auth.repository.IncomeRepository;
import com.example.auth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditService {
    private final UserRepository userRepository;
    private final DebtRepository debtRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

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

//    @Transactional
//    public Debt repay(Expense expense, Long creditId) {
//        User user = getCurrentUser();
//        Debt credit = getById(creditId);
//        credit.subtractMoney(expense.getAmount());
//    }

}
