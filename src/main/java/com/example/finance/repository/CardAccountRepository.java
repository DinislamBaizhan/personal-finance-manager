package com.example.finance.repository;

import com.example.finance.data.entity.CardAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardAccountRepository extends JpaRepository<CardAccount, Long> {
    Optional<CardAccount> findByIdAndUserId(Long cardId, Long userId);

    Optional<List<CardAccount>> findAllByUserId(Long userId);
}
