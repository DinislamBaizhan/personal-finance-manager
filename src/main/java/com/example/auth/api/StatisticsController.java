package com.example.auth.api;

import com.example.auth.data.dto.TransactionDTO;
import com.example.auth.data.entity.Expense;
import com.example.auth.data.entity.Income;
import com.example.auth.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("for-day")
    public TransactionDTO getForDay(
            @RequestParam("before") LocalDateTime before,
            @RequestParam("after") LocalDateTime after) {
        return statisticsService.getAllForDay(before, after);
    }

    @GetMapping("for-day/incomes")
    public List<Income> getForDayIncomes(
            @RequestParam("before") LocalDateTime before,
            @RequestParam("after") LocalDateTime after) {
        return statisticsService.getIncomesForDay(before, after);
    }

    @GetMapping("for-day/expenses")
    public List<Expense> getForDayExpenses(
            @RequestParam("before") LocalDateTime before,
            @RequestParam("after") LocalDateTime after) {
        return statisticsService.getExpensesForDay(before, after);
    }
}