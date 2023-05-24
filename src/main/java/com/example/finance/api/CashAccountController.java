package com.example.finance.api;

import com.example.finance.data.entity.CashAccount;
import com.example.finance.data.entity.Expense;
import com.example.finance.data.entity.Income;
import com.example.finance.data.enums.AccountType;
import com.example.finance.service.CashAccountService;
import com.example.finance.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/v1/cash-account")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cash account controller", description = "Cash account management")
public class CashAccountController {

    private final CashAccountService cashAccountService;
    private final TransferService transferService;

    @PostMapping
    @Operation(summary = "Create a new cash account")
    @ApiResponse(responseCode = "201", description = "Card account successfully created")
    public ResponseEntity<CashAccount> save(@RequestBody CashAccount cashAccount) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cashAccountService.save(cashAccount));
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
    public CashAccount getById(@RequestBody Expense expense, @Parameter(description = "ID for Category", required = true) @RequestParam Long categoryId) {
        return cashAccountService.subtractMoney(expense, categoryId);
    }

    @PatchMapping("/{fromId}/transfer/{toId}")
    @Operation(summary = "Transfer money between cash accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully transferred money")
    })
    public CashAccount transferAccounts(
            @Parameter(description = "Id of the account from which the money is transferred") @PathVariable Long fromId,
            @Parameter(description = "Id of the account to which the money is being transferred") @PathVariable Long toId,
            @Parameter(description = "Type of the account to transfer money from/to: CASH or CARD") @RequestParam("accountType") AccountType accountType,
            @Parameter(description = "Amount of money to transfer") @RequestParam("amount") BigDecimal amount) {
        return transferService.transferFromCashAccount(fromId, toId, accountType, amount);
    }

    @PatchMapping("/{cashId}/limit")
    @Operation(summary = "Set account limit")
    public BigDecimal setLimit(
            @Parameter(description = "Id for cash account") @PathVariable Long cashId,
            @Parameter(description = "Limit amount") @RequestParam("limit") BigDecimal limit) {
        return cashAccountService.setLimit(cashId, limit);
    }

    @DeleteMapping("/{cashId}")
    public void delete(@PathVariable Long cashId) {
        cashAccountService.delete(cashId);
    }
}
