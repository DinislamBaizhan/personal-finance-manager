package com.example.auth.api;

import com.example.auth.data.entity.Goals;
import com.example.auth.service.FinanceGoalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class FinanceGoalsController {

    private final FinanceGoalsService goalsService;

    @GetMapping
    public List<Goals> getAllGoals() {
        return goalsService.getAllGoals();
    }

    @PostMapping
    public Goals createGoal(@RequestBody Goals goal) {
        return goalsService.createGoal(goal);
    }

    @GetMapping("/{id}")
    public Goals getGoalById(@PathVariable Long id) {
        return goalsService.getGoalById(id);
    }

    @PutMapping("/{id}")
    public Goals updateGoal(@PathVariable Long id, @RequestBody Goals updatedGoal) {
        return goalsService.updateGoal(id, updatedGoal);
    }

    @DeleteMapping("/{id}")
    public void deleteGoal(@PathVariable Long id) {
        goalsService.deleteGoal(id);
    }

}
