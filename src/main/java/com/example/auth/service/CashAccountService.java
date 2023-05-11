package com.example.auth.service;

import com.example.auth.data.entity.CashAccount;
import com.example.auth.data.entity.User;
import com.example.auth.repository.CashAccountRepository;
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
public class CashAccountService {
    private final UserRepository userRepository;
    private final CashAccountRepository cashRepository;

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
}
