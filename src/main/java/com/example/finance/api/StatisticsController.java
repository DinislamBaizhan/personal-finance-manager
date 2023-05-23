package com.example.finance.api;

import com.example.finance.data.dto.TransactionDTO;
import com.example.finance.data.entity.Expense;
import com.example.finance.data.entity.Income;
import com.example.finance.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Statistics controller", description = "Obtaining information about monetary transactions in a period of time Input Format: (2023-04-17T09:16:59)")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("for-day")
    @Operation(summary = "Statistics for 24 h", description = "Get statistics for 1 day. Input Format: (2023-04-17T09:16:59)")
    @ApiResponse(responseCode = "200", description = "Successfully received statistics for 1 day.")
    public TransactionDTO getForDay(
            @Parameter(description = "Start date and time") @RequestParam("before") LocalDateTime before,
            @Parameter(description = "End date and time") @RequestParam("after") LocalDateTime after) {
        return statisticsService.getAllForDay(before, after);
    }

    @GetMapping("for-day/incomes")
    @Operation(summary = "Incomes for 24 h", description = "Get income statistics for 1 day. Input Format: (2023-04-17T09:16:59)")
    @ApiResponse(responseCode = "200", description = "Successfully received income statistics for 1 day")
    public List<Income> getForDayIncomes(
            @Parameter(description = "Start date and time") @RequestParam("before") LocalDateTime before,
            @Parameter(description = "End date and time") @RequestParam("after") LocalDateTime after) {
        return statisticsService.getIncomesForDay(before, after);
    }

    @GetMapping("for-day/expenses")
    @Operation(summary = "Expenses for 24 h", description = "Get expense statistics for 1 day. Input Format: (2023-04-17T09:16:59)")
    @ApiResponse(responseCode = "200", description = "Successfully received expense statistics for 1 day.")
    public List<Expense> getForDayExpenses(
            @Parameter(description = "Start date and time") @RequestParam("before") LocalDateTime before,
            @Parameter(description = "End date and time") @RequestParam("after") LocalDateTime after) {
        return statisticsService.getExpensesForDay(before, after);
    }
}