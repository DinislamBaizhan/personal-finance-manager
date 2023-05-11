package com.example.auth.api;

import com.example.auth.data.entity.Debt;
import com.example.auth.repository.CategoryRepository;
import com.example.auth.repository.IncomeRepository;
import com.example.auth.repository.TransactionRepository;
import com.example.auth.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/loan")
public class LoanController {

    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    private final LoanService loanService;

    @PostMapping
    public Debt save(@RequestBody Debt debt) {
        return loanService.save(debt);
    }

    @GetMapping
    public List<Debt> getAll() {
        return loanService.getAll();
    }

    @GetMapping("/{loanId}")
    public Debt getById(@PathVariable Long loanId) {
        return loanService.getById(loanId);
    }


    @GetMapping("/true")
    public List<Debt> getAllIsActiveTrue() {
        return loanService.getAllActive();
    }

    @GetMapping("/false")
    public List<Debt> getAllIsActiveFalse() {
        return loanService.getAllNotActive();
    }


//    @PostMapping("/income")
//    public Income createIncome(@RequestBody Income income) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User user = userRepository.findByEmail(authentication.getName()).get();
//        Category category = categoryRepository.findById(1L).get();
//
//        income.setUser(user);
//        income.setCategory(category);
//
//
//        return incomeRepository.save(income);
//    }
//
//    @GetMapping("/income")
//    public List<Income> getIncome() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User user = userRepository.findByEmail(authentication.getName()).get();
//
//        return incomeRepository.findAllByUserIdAndCardId(user.getId(), 2L);
//    }
//
//    @GetMapping("/t/{cardId}")
//    public List<Transaction> trans(@PathVariable Long cardId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User user = userRepository.findByEmail(authentication.getName()).get();
//
//        return transactionRepository.findAllByUserIdAndCardId(user.getId(), cardId);
//    }
}
