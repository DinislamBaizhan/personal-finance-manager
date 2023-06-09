package com.example.finance.repository;

import com.example.finance.data.entity.Debt;
import com.example.finance.data.enums.DebtType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> getDebtsByDebtTypeAndUserId(DebtType debtType, Long userId);

    Optional<Debt> findByDebtTypeAndUserIdAndId(DebtType debtType, Long userId, Long debtId);

    List<Debt> findAllByDebtTypeAndUserIdAndActiveIsTrue(DebtType debtType, Long userId);

    List<Debt> findAllByDebtTypeAndUserIdAndActiveIsFalse(DebtType debtType, Long userId);
}
