package com.example.auth.service;

import com.example.auth.data.dto.TransactionDTO;
import com.example.auth.data.entity.Expense;
import com.example.auth.data.entity.Income;
import com.example.auth.data.entity.Transaction;
import com.example.auth.data.entity.User;
import com.example.auth.repository.ExpenseRepository;
import com.example.auth.repository.IncomeRepository;
import com.example.auth.repository.TransactionRepository;
import com.example.auth.repository.UserRepository;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final TransactionRepository transactionRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    public TransactionDTO getAllForDay(LocalDateTime before, LocalDateTime after) {

        User user = getCurrentUser();

        List<Income> incomeList = incomeRepository.findAllByUserIdAndCreatedAtBetween(user.getId(), before, after);
        List<Expense> expenseList = expenseRepository.findAllByUserIdAndCreatedAtBetween(user.getId(), before, after);

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setIncome(incomeList);
        transactionDTO.setExpense(expenseList);
        return transactionDTO;
    }

    public List<Income> getIncomesForDay(LocalDateTime before, LocalDateTime after) {
        User user = getCurrentUser();

        return incomeRepository.findAllByUserIdAndCreatedAtBetween(user.getId(), before, after);
    }

    public List<Expense> getExpensesForDay(LocalDateTime before, LocalDateTime after) {
        User user = getCurrentUser();
        return expenseRepository.findAllByUserIdAndCreatedAtBetween(user.getId(), before, after);
    }

    public File generateCVS(LocalDateTime before, LocalDateTime after) throws IOException {

        String fileName = "transactions " + before + "after " + after + ".csv";
        String path = Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).getPath();

        List<Expense> expenseList = getExpensesForDay(before, after);
        List<Income> incomeList = getIncomesForDay(before, after);

        List<Transaction> combinedList = new ArrayList<>();
        combinedList.addAll(expenseList);
        combinedList.addAll(incomeList);

        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
            String[] headers = {"date", "account name", "account type", "amount", "description", "category"};
            writer.writeNext(headers);

            for (Transaction transaction : combinedList) {
                String[] data = {
                        String.valueOf(transaction.getCreatedAt()),
                        transaction.getAccountName(),
                        String.valueOf(transaction.getAccountType()),
                        String.valueOf(transaction.getAmount()),
                        transaction.getDescription(),
                        transaction.getCategory().getName()
                };
                writer.writeNext(data);
            }
        } catch (IOException ex) {
            throw new IOException("failed to csv");
        }
        return new File(path);
    }
}