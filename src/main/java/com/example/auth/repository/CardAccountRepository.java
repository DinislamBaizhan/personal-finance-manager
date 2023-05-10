package com.example.auth.repository;

import com.example.auth.data.entity.CardAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardAccountRepository extends JpaRepository<CardAccount, Long> {
    Optional<CardAccount> findByIdAndUserId(Long cardId, Long userId);

    List<CardAccount> findByUserId(Long userId);
}
