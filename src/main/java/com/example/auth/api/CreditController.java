package com.example.auth.api;

import com.example.auth.data.entity.Debt;
import com.example.auth.data.entity.User;
import com.example.auth.data.enums.DebtType;
import com.example.auth.repository.DebtRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/credit")
public class CreditController {
    private final DebtRepository debtRepository;
    private final UserRepository userRepository;

    @GetMapping("/{creditId}")
    public Debt get(@PathVariable Long creditId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).get();

        return debtRepository.findByDebtTypeAndUserIdAndId(DebtType.CREDIT, user.getId(), creditId).get();
    }

    @GetMapping
    public List<Debt> getAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).get();

        return debtRepository.getDebtsByDebtTypeAndUserId(DebtType.CREDIT, user.getId());
    }

    @GetMapping("/true")
    public List<Debt> getAllIsActiveTrue() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).get();

        return debtRepository.findAllByDebtTypeAndUserIdAndActiveIsTrue(DebtType.CREDIT, user.getId());
    }

    @GetMapping("/false")
    public List<Debt> getAllIsActiveFalse() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).get();

        return debtRepository.findAllByDebtTypeAndUserIdAndActiveIsFalse(DebtType.CREDIT, user.getId());
    }

    @PostMapping
    public Debt getAllIsActive(@RequestBody Debt debt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).get();
        debt.setDebtType(DebtType.CREDIT);
        debt.setUser(user);

        return debtRepository.save(debt);
    }
}