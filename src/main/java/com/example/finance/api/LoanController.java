package com.example.finance.api;

import com.example.finance.data.entity.Debt;
import com.example.finance.data.entity.Expense;
import com.example.finance.service.CreditService;
import com.example.finance.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/loan")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Loan controller", description = "Loan management")
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
    public ResponseEntity<Debt> save(@RequestBody Debt debt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.save(debt));
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
    @Operation(summary = "Repay a loan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan repaid successfully"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    public Debt repay(@RequestBody Expense expense, @Parameter(description = "ID of the Loan") @PathVariable Long loanId) {
        return loanService.repay(expense, loanId);
    }

    @PostMapping("/{loanId}/increase-loan")
    @Operation(summary = "Increase loan amount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan amount increased successfully"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    public Debt increaseLoan(@Parameter(description = "Id of the Loan") @PathVariable Long loanId, @Parameter(description = "Amount to increase") @RequestParam BigDecimal amount) {
        return loanService.increaseLoan(loanId, amount);
    }

    @PatchMapping("/{loanId}/activity")
    @Operation(summary = "Set loan activity status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan activity status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    public boolean inactive(@Parameter(description = "ID of the loan") @PathVariable Long loanId, @Parameter(description = "condition of the status") @RequestParam boolean condition) {
        return creditService.setActivity(loanId, condition);
    }

    @DeleteMapping("/{loanId}")
    public void delete(@PathVariable Long loanId) {
        loanService.delete(loanId);
    }
}
