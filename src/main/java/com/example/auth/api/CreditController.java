package com.example.auth.api;

import com.example.auth.data.entity.Debt;
import com.example.auth.data.entity.Expense;
import com.example.auth.service.CreditService;
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
@RequestMapping("/api/v1/credit")
@SecurityRequirement(name = "bearerAuth")
public class CreditController {

    private final CreditService creditService;

    @PostMapping
    @Operation(summary = "Save a new debt", description = "Creates a new debt record.")
    @ApiResponse(responseCode = "201", description = "The debt was created successfully.")
    public Debt save(@RequestBody Debt debt) {
        return creditService.save(debt);
    }

    @GetMapping
    @Operation(summary = "Get all debts", description = "Retrieve a list of all debts.")
    @ApiResponse(responseCode = "200", description = "List of all debts successfully retrieved.")
    public List<Debt> getAll() {
        return creditService.getAll();
    }

    @GetMapping("/{creditId}")
    @Operation(summary = "Get debt by ID", description = "Retrieve a debt by its ID.")
    @ApiResponse(responseCode = "200", description = "Debt successfully retrieved.")
    @ApiResponse(responseCode = "404", description = "Debt not found.")
    public Debt getById(@Parameter(description = "ID of the debt to retrieve") @PathVariable Long creditId) {
        return creditService.getById(creditId);
    }


    @GetMapping("/true")
    @Operation(summary = "Get all active debts", description = "Retrieve a list of all active debts.")
    @ApiResponse(responseCode = "200", description = "List of all active debts successfully retrieved.")
    public List<Debt> getAllIsActiveTrue() {
        return creditService.getAllActive();
    }

    @GetMapping("/false")
    @Operation(summary = "Get all inactive debts", description = "Retrieve a list of all inactive debts.")
    @ApiResponse(responseCode = "200", description = "List of all inactive debts successfully retrieved.")
    public List<Debt> getAllIsActiveFalse() {
        return creditService.getAllNotActive();
    }

    @PostMapping("/{creditId}/repay")
    @Operation(summary = "Repay a credit", description = "Repay a credit by credit ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credit repaid successfully"),
            @ApiResponse(responseCode = "404", description = "Credit not found")
    })
    public Debt repay(@RequestBody Expense expense, @Parameter(description = "ID of the credit") @PathVariable Long creditId) {
        return creditService.repay(expense, creditId);
    }

    @PostMapping("/{creditId}/increase-credit")
    @Operation(summary = "Increase credit amount", description = "Increase the credit amount by providing the credit ID and amount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credit amount increased successfully"),
            @ApiResponse(responseCode = "404", description = "Credit not found")
    })
    public Debt increaseLoan(@Parameter(description = "ID of the credit") @PathVariable Long creditId, @Parameter(description = "Amount to increase credit") @RequestParam BigDecimal amount) {
        return creditService.increaseCredit(creditId, amount);
    }

    @PatchMapping("/{creditId}/activity")
    @Operation(summary = "Set credit activity status", description = "Set the activity status of a credit by providing the credit ID and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credit activity status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Credit not found")
    })
    public boolean inactive(@Parameter(description = "ID of the credit") @PathVariable Long creditId, @Parameter(description = "Activity status") @RequestParam boolean status) {
        return creditService.setActivity(creditId, status);
    }
}