package com.example.auth.api;

import com.example.auth.data.entity.CashAccount;
import com.example.auth.data.entity.Expense;
import com.example.auth.data.entity.Income;
import com.example.auth.service.CashAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/cash-account")
public class CashAccountController {

    private final CashAccountService cashAccountService;

    @PostMapping
    public CashAccount post(@RequestBody CashAccount cashAccount) {
        return cashAccountService.save(cashAccount);
    }

    @GetMapping
    public List<CashAccount> getAll() {
        return cashAccountService.getAll();
    }

    @GetMapping("/{accountId}")
    public CashAccount getById(@PathVariable Long accountId) {
        return cashAccountService.getById(accountId);
    }

    @PatchMapping("/update/{cashId}/balance")
    public CashAccount updateBalance(@PathVariable Long cashId,
                                     @RequestParam("balance") BigDecimal balance) {
        return cashAccountService.switchBalance(cashId, balance);
    }

    @PatchMapping("/add/balance")
    public CashAccount addMoney(@RequestBody Income income, @RequestParam Long categoryId) {
        return cashAccountService.addMoney(income, categoryId);
    }

    @PatchMapping("/subtract/balance")
    public CashAccount getById(@RequestBody Expense expense, @RequestParam Long categoryId) throws Exception {
        return cashAccountService.subtractMoney(expense, categoryId);
    }
}
