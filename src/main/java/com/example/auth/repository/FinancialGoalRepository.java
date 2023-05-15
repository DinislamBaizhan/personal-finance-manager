package com.example.auth.repository;

import com.example.auth.data.entity.Goals;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialGoalRepository extends JpaRepository<Goals, Long> {
}
