package com.example.auth.repository;

import com.example.auth.data.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findAllByUserIdAndAccountId(Long userId, Long accountId);

    List<Income> findAllByUserId(Long userId);

    List<Income> findAllByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime finish);
}
