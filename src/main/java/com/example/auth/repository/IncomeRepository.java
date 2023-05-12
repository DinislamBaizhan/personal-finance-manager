package com.example.auth.repository;

import com.example.auth.data.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findAllByUserIdAndCardId(Long userId, Long cardId);

}
