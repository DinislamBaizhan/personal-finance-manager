package com.example.finance.repository;

import com.example.finance.data.entity.CashAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CashAccountRepository extends JpaRepository<CashAccount, Long> {

    Optional<CashAccount> findByIdAndUserId(Long accountId, Long userId);

    Optional<List<CashAccount>> findAllByUserId(Long userId);
}
