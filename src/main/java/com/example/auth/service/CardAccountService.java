package com.example.auth.service;

import com.example.auth.data.entity.CardAccount;
import com.example.auth.data.entity.User;
import com.example.auth.repository.CardAccountRepository;
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
public class CardAccountService {
    private final UserRepository userRepository;
    private final CardAccountRepository cardAccountRepository;

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
}
