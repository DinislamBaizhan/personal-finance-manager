package com.example.auth.repository;

import com.example.auth.data.entity.CashAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CashAccountRepository extends JpaRepository<CashAccount, Long> {

    Optional<CashAccount> findByIdAndUserId(Long accountId, Long userId);

    List<CashAccount> findAllByUserId(Long userId);
}
