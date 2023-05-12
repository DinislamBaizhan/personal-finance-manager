package com.example.auth.api;

import com.example.auth.data.entity.Debt;
import com.example.auth.service.CreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/credit")
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

//    @PostMapping("/{creditId}/repay")
//    public Debt repay(@RequestBody Expense expense, @PathVariable Long creditId) {
//
//    }
}