package com.example.auth.api;

import com.example.auth.data.entity.CardAccount;
import com.example.auth.service.CardAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

}
