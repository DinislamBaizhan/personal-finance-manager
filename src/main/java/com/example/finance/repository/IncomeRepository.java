package com.example.finance.repository;

import com.example.finance.data.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findAllByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime finish);
}
