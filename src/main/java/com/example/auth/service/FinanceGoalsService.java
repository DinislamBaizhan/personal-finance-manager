package com.example.auth.service;

import com.example.auth.data.entity.Goals;
import com.example.auth.repository.FinancialGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceGoalsService {

    private final FinancialGoalRepository goalRepository;

    public List<Goals> getAllGoals() {
        return goalRepository.findAll();
    }

    public Goals createGoal(Goals goal) {
        return goalRepository.save(goal);
    }

    public Goals getGoalById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));
    }

    public Goals updateGoal(Long id, Goals updatedGoal) {
        Goals goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));

        goal.setGoalType(updatedGoal.getGoalType());
        goal.setTargetAmount(updatedGoal.getTargetAmount());
        goal.setCurrentAmount(updatedGoal.getCurrentAmount());
        goal.setBudgetCategories(updatedGoal.getBudgetCategories());
        goal.setBudgetPeriod(updatedGoal.getBudgetPeriod());
        goal.setCurrency(updatedGoal.getCurrency());

        return goalRepository.save(goal);
    }

    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }
}
