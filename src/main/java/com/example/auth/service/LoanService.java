package com.example.auth.service;

import com.example.auth.data.entity.Debt;
import com.example.auth.data.entity.User;
import com.example.auth.data.enums.DebtType;
import com.example.auth.repository.DebtRepository;
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
public class LoanService {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;

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
}
