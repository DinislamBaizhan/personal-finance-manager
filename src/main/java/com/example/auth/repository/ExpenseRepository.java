package com.example.auth.repository;

import com.example.auth.data.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime finish);
}
