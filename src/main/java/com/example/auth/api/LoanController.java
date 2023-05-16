package com.example.auth.api;

import com.example.auth.data.entity.Debt;
import com.example.auth.data.entity.Expense;
import com.example.auth.service.CreditService;
import com.example.auth.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/loan")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {

    private final LoanService loanService;
    private final CreditService creditService;

    @PostMapping
    @Operation(summary = "Create a new loan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved the debt"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Debt save(@RequestBody Debt debt) {
        return loanService.save(debt);
    }

    @GetMapping
    @Operation(summary = "Get all loans")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all debts"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Debt> getAll() {
        return loanService.getAll();
    }

    @GetMapping("/{loanId}")
    @Operation(summary = "Get a loan by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the debt"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Debt not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Debt getById(@Parameter(description = "ID of the loan to retrieve") @PathVariable Long loanId) {
        return loanService.getById(loanId);
    }


    @GetMapping("/true")
    @Operation(summary = "Get all active loans")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved active debts"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Debt> getAllIsActiveTrue() {
        return loanService.getAllActive();
    }

    @GetMapping("/false")
    @Operation(summary = "Get all inactive loans")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved inactive debts"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Debt> getAllIsActiveFalse() {
        return loanService.getAllNotActive();
    }

    @PostMapping("/{loanId}/repay")
    public Debt repay(@RequestBody Expense expense, @PathVariable Long loanId) {
        return loanService.repay(expense, loanId);
    }

    @PostMapping("/{loanId}/increase-loan")
    public Debt increaseLoan(@PathVariable Long loanId, @RequestParam BigDecimal amount) {
        return loanService.increaseLoan(loanId, amount);
    }

    @PatchMapping("/{loanId}/activity")
    public boolean inactive(@PathVariable Long loanId, @RequestParam boolean condition) {
        return creditService.setActivity(loanId, condition);
    }
}
