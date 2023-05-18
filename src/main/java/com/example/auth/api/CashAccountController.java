package com.example.auth.api;

import com.example.auth.data.entity.CashAccount;
import com.example.auth.data.entity.Expense;
import com.example.auth.data.entity.Income;
import com.example.auth.data.enums.AccountType;
import com.example.auth.service.CashAccountService;
import com.example.auth.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/cash-account")
@SecurityRequirement(name = "bearerAuth")
public class CashAccountController {

    private final CashAccountService cashAccountService;
    private final TransferService transferService;

    @PostMapping
    @Operation(summary = "Create a new cash account")
    public CashAccount post(@RequestBody CashAccount cashAccount) {
        return cashAccountService.save(cashAccount);
    }

    @GetMapping
    @Operation(summary = "Get all cash accounts")
    public List<CashAccount> getAll() {
        return cashAccountService.getAll();
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get a cash account by ID")
    @ApiResponse(responseCode = "200", description = "The cash account with the given ID",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CashAccount.class)))
    @ApiResponse(responseCode = "404", description = "Cash account not found")
    public CashAccount getById(@PathVariable Long accountId) {
        return cashAccountService.getById(accountId);
    }

    @PatchMapping("/update/{cashId}/balance")
    @Operation(summary = "Update the balance of a cash account")
    @ApiResponse(responseCode = "200", description = "The cash account with the updated balance",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CashAccount.class)))
    @ApiResponse(responseCode = "404", description = "Cash account not found")
    public CashAccount updateBalance(@PathVariable Long cashId,
                                     @Parameter(description = "New balance value", required = true)
                                     @RequestParam("balance") BigDecimal balance) {
        return cashAccountService.switchBalance(cashId, balance);
    }

    @PatchMapping("/add/balance")
    @Operation(summary = "Add money to a cash account")
    @ApiResponse(responseCode = "200", description = "The cash account with the added money",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CashAccount.class)))
    @ApiResponse(responseCode = "404", description = "Cash account or category not found")
    public CashAccount addMoney(@RequestBody Income income, @Parameter(description = "Category ID for the income", required = true) @RequestParam Long categoryId) {
        return cashAccountService.addMoney(income, categoryId);
    }

    @PatchMapping("/subtract/balance")
    @Operation(summary = "Subtract money from a cash account")
    @ApiResponse(responseCode = "200", description = "The cash account with the subtracted money",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CashAccount.class)))
    @ApiResponse(responseCode = "400", description = "Insufficient balance or invalid expense data")
    @ApiResponse(responseCode = "404", description = "Cash account is not found")
    public CashAccount getById(@RequestBody Expense expense, @Parameter(description = "ID for Category", required = true) @RequestParam Long categoryId) throws Exception {
        return cashAccountService.subtractMoney(expense, categoryId);
    }

    @PatchMapping("/{fromId}/transfer/{toId}")
    @Operation(summary = "Transfer money between cash accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully transferred money")
    })
    public CashAccount transferAccounts(@PathVariable Long fromId,
                                        @PathVariable Long toId,
                                        @Parameter(description = "Type of the account to transfer money from/to") @RequestParam("accountType") AccountType accountType,
                                        @Parameter(description = "Amount of money to transfer") @RequestParam("amount") BigDecimal amount) {
        return transferService.transferFromCashAccount(fromId, toId, accountType, amount);
    }
}
