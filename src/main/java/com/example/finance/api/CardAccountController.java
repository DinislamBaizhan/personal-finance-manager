package com.example.finance.api;

import com.example.finance.data.entity.CardAccount;
import com.example.finance.data.entity.Expense;
import com.example.finance.data.entity.Income;
import com.example.finance.data.enums.AccountType;
import com.example.finance.service.CardAccountService;
import com.example.finance.service.TransferService;
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
@RequestMapping("/api/v1/card-account")
@Tag(name = "Card account controller", description = "Card account management")
@SecurityRequirement(name = "bearerAuth")
public class CardAccountController {
    private final CardAccountService cardAccountService;
    private final TransferService transferService;

    @PostMapping
    @Operation(summary = "Create a new card account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card account created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<CardAccount> save(@RequestBody CardAccount cardAccount) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardAccountService.save(cardAccount));
    }

    @GetMapping
    @Operation(summary = "Get all card accounts")
    @ApiResponse(responseCode = "200", description = "OK")
    public List<CardAccount> getAll() {
        return cardAccountService.getAll();
    }

    @GetMapping("/{cardId}")
    @Operation(summary = "Get a card account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Card account not found")
    })
    public CardAccount getById(@PathVariable @Parameter(description = "ID of the card account") Long cardId) {
        return cardAccountService.getById(cardId);
    }

    @PatchMapping("/update/{cardId}/balance")
    @Operation(summary = "Update the balance of a card account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Card account not found")
    })
    public ResponseEntity<CardAccount> updateBalance(@PathVariable @Parameter(description = "ID of the card account to update") Long cardId,
                                                     @RequestParam("balance") @Parameter(description = "New balance value to set") BigDecimal balance) {
        return ResponseEntity.ok(cardAccountService.switchBalance(cardId, balance));
    }

    @PatchMapping("/add/balance")
    @Operation(summary = "Add money to a card account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Card account not found")
    })
    public ResponseEntity<CardAccount> addMoney(
            @RequestBody Income income,
            @RequestParam @Parameter(description = "ID of the category to assign to the income") Long categoryId) {
        return ResponseEntity.ok(cardAccountService.addMoney(income, categoryId));
    }

    @PatchMapping("/subtract/balance")
    @Operation(summary = "Subtract money from a card account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Card account not found")
    })
    public ResponseEntity<CardAccount> subtract(
            @RequestBody Expense expense,
            @RequestParam @Parameter(description = "ID of the category to assign to the expense") Long categoryId) {
        return ResponseEntity.ok(cardAccountService.subtractMoney(expense, categoryId));
    }

    @PatchMapping("/{fromId}/transfer/{toId}")
    @Operation(summary = "Transfer funds between card accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully transfer"),
            @ApiResponse(responseCode = "404", description = "Card accounts not found")
    })
    public ResponseEntity<CardAccount> transferAccounts(
            @Parameter(description = "Id of the account from which the money is transferred") @PathVariable Long fromId,
            @Parameter(description = "Id of the account to which the money is being transferred") @PathVariable Long toId,
            @Parameter(description = "Type of the account to transfer money from/to: CASH or CARD") @RequestParam("accountType") AccountType accountType,
            @Parameter(description = "Amount of money to transfer") @RequestParam("amount") BigDecimal amount) {
        return ResponseEntity.ok(transferService.transferFromCardAccount(fromId, toId, accountType, amount));
    }

    @PatchMapping("/{cardId}/limit")
    @Operation(summary = "Set account limit")
    public BigDecimal setLimit(
            @Parameter(description = "id for cash account") @PathVariable Long cardId,
            @Parameter(description = "Limit amount") @RequestParam("limit") BigDecimal limit) {
        return cardAccountService.setLimit(cardId, limit);
    }

    @DeleteMapping()
    public void delete(@RequestParam("cardId") Long cardId) {
        cardAccountService.delete(cardId);
    }
}
