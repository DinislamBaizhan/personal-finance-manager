package com.example.auth.api;

import com.example.auth.data.entity.CardAccount;
import com.example.auth.data.entity.Expense;
import com.example.auth.data.entity.Income;
import com.example.auth.service.CardAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/card-account")
public class CardAccountController {

    private final CardAccountService cardAccountService;

    @PostMapping
    public CardAccount post(@RequestBody CardAccount cardAccount) {
        return cardAccountService.save(cardAccount);
    }

    @GetMapping
    public List<CardAccount> getAll() {
        return cardAccountService.getAll();
    }

    @GetMapping("/{cardId}")
    public CardAccount getById(@PathVariable Long cardId) {
        return cardAccountService.getById(cardId);
    }

    @PatchMapping("/update/{cardId}/balance")
    public CardAccount updateBalance(@PathVariable Long cardId,
                                     @RequestParam("balance") BigDecimal balance) {
        return cardAccountService.switchBalance(cardId, balance);
    }

    @PatchMapping("/add/balance")
    public CardAccount addMoney(@RequestBody Income income, @RequestParam Long categoryId) {
        return cardAccountService.addMoney(income, categoryId);
    }

    @PatchMapping("/subtract/balance")
    public CardAccount getById(@RequestBody Expense expense, @RequestParam Long categoryId) {
        return cardAccountService.subtractMoney(expense, categoryId);
    }
}
