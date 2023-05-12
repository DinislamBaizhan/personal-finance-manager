package com.example.auth.api;

import com.example.auth.data.entity.Debt;
import com.example.auth.repository.CategoryRepository;
import com.example.auth.repository.IncomeRepository;
import com.example.auth.repository.TransactionRepository;
import com.example.auth.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/loan")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {

    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    private final LoanService loanService;

    @PostMapping
    @Operation(summary = "Create a new loan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved the debt"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Debt save(@RequestBody Debt debt) {
        return loanService.save(debt);
    }

    @GetMapping
    @Operation(summary = "Get all loans")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all debts"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Debt> getAll() {
        return loanService.getAll();
    }

    @GetMapping("/{loanId}")
    @Operation(summary = "Get a loan by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the debt"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Debt not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Debt getById(@Parameter(description = "ID of the loan to retrieve") @PathVariable Long loanId) {
        return loanService.getById(loanId);
    }


    @GetMapping("/true")
    @Operation(summary = "Get all active loans")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved active debts"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Debt> getAllIsActiveTrue() {
        return loanService.getAllActive();
    }

    @GetMapping("/false")
    @Operation(summary = "Get all inactive loans")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved inactive debts"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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
