package com.example.auth.api;

import com.example.auth.data.entity.CashAccount;
import com.example.auth.service.CashAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
