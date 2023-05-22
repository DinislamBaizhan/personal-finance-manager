package com.example.finance.service;

import com.example.finance.data.dto.TransactionDTO;
import com.example.finance.data.entity.Expense;
import com.example.finance.data.entity.Income;
import com.example.finance.data.entity.Transaction;
import com.example.finance.data.entity.User;
import com.example.finance.repository.ExpenseRepository;
import com.example.finance.repository.IncomeRepository;
import com.example.finance.repository.TransactionRepository;
import com.example.finance.repository.UserRepository;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final TransactionRepository transactionRepository;

    private static Path getPath(LocalDateTime before, LocalDateTime after) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String fileName = "csv/transactions_" + before + "_after_" + after + "_" + timestamp + ".csv";
        fileName = fileName.replace(':', '_');
        Path filePath = Paths.get(fileName);

        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }

        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        return filePath;
    }

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

    public File generateCSV(LocalDateTime before, LocalDateTime after) throws IOException {

        Path filePath = getPath(before, after);

        List<Expense> expenseList = getExpensesForDay(before, after);
        List<Income> incomeList = getIncomesForDay(before, after);

        List<Transaction> combinedList = new ArrayList<>();
        combinedList.addAll(expenseList);
        combinedList.addAll(incomeList);

        try (CSVWriter writer = new CSVWriter(Files.newBufferedWriter(filePath))) {
            String[] headers = {"date", "account name", "account type", "amount", "description", "category", "transaction type"};
            writer.writeNext(headers);

            for (Transaction transaction : combinedList) {
                String[] data = {
                        String.valueOf(transaction.getCreatedAt()),
                        transaction.getAccountName(),
                        String.valueOf(transaction.getAccountType()),
                        String.valueOf(transaction.getAmount()),
                        transaction.getDescription(),
                        transaction.getCategory().getName(),
                        String.valueOf(transaction.getTransactionType())
                };
                writer.writeNext(data);
            }
        } catch (IOException ex) {
            throw new IOException("Failed to create CSV file.");
        }
        return filePath.toFile();
    }
}