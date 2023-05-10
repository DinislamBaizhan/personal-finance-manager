package com.example.auth.api;

import com.example.auth.data.entity.CashAccount;
import com.example.auth.data.entity.User;
import com.example.auth.repository.CashAccountRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/cash-account")
public class CashAccountController {

    private final CashAccountRepository cashAccountRepository;
    private final UserRepository userRepository;

    @GetMapping("/{accountId}")
    private CashAccount get(@PathVariable Long accountId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).get();

        return cashAccountRepository.findByIdAndUserId(accountId, user.getId()).get();

    }

    @GetMapping
    private List<CashAccount> getAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).get();

        return cashAccountRepository.findAllByUserId(user.getId());

    }

    @PostMapping
    private CashAccount post(@RequestBody CashAccount cashAccount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).get();

        cashAccount.setUser(user);
        return cashAccountRepository.save(cashAccount);

    }
}
