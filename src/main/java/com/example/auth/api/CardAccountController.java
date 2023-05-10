package com.example.auth.api;

import com.example.auth.data.entity.CardAccount;
import com.example.auth.data.entity.User;
import com.example.auth.repository.CardAccountRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/card-account")
public class CardAccountController {

    private final CardAccountRepository cardAccountRepository;
    private final UserRepository userRepository;

    @GetMapping
    public List<CardAccount> get() {
        var ss = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(ss.getName()).get();

        return cardAccountRepository.findByUserId(user.getId());
    }

    @PostMapping
    public CardAccount post(@RequestBody CardAccount cardAccount) {
        var ss = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(ss.getName()).get();
        cardAccount.setUser(user);

        return cardAccountRepository.save(cardAccount);
    }
}
