package com.example.finance.service;

import com.example.finance.data.dto.RegisterDTO;
import com.example.finance.data.dto.UserDTO;
import com.example.finance.data.entity.*;
import com.example.finance.data.enums.DebtType;
import com.example.finance.data.enums.Role;
import com.example.finance.repository.ConfirmationTokenRepository;
import com.example.finance.repository.DebtRepository;
import com.example.finance.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final DebtRepository debtRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final ConfirmationTokenRepository confirmationTokenRepository;


    public User getAuthenticatedProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        return repository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("profile not found")
        );
    }

    public UserDTO getDTO() {
        User user = getAuthenticatedProfile();
        return new UserDTO(
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                getSumOfAllMoney(
                        user.getCardAccounts(),
                        user.getCashAccounts()),
                getAllDebtSum(user.getId())
        );
    }

    public BigDecimal getSumOfAllMoney(List<CardAccount> cardAccounts, List<CashAccount> cashAccounts) {
        BigDecimal sum1 = cardAccounts.stream()
                .map(CardAccount::getBalance)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sum2 = cashAccounts.stream()
                .map(CashAccount::getBalance)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum1.add(sum2);
    }

    public BigDecimal getAllDebtSum(Long userId) {
        List<Debt> debts = debtRepository.findAllByDebtTypeAndUserIdAndActiveIsTrue(DebtType.CREDIT, userId);
        return debts.stream()
                .map(Debt::getIndebtedness)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> {
                    log.error("user not found %s" + email);
                    return new EntityNotFoundException("profile not found");
                }
        );
    }

    public User updPassword(User profile, String password) throws Exception {
        profile.setPassword(passwordEncoder.encode(password));
        try {
            return repository.save(profile);
        } catch (Exception e) {
            throw new Exception("fail to update password " + e.getCause());
        }
    }

    public UserDTO rename(List<String> name) {
        User user = getAuthenticatedProfile();
        user.setFirstname(name.get(0));
        user.setLastname(name.get(1));
        log.info("rename: firstname - %s" + name.get(0), "lastname - %s" + name.get(1));
        repository.save(user);
        return getDTO();
    }

    public User saveNewUser(RegisterDTO request) {
        User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(false);

        try {
            return repository.save(user);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to save user with email: " + user.getEmail(), ex);
        }
    }

    public User save(User profile) {
        try {
            return repository.save(profile);
        } catch (DataAccessException e) {
            log.error("Failed to save to database: " + profile, e);
            throw new RuntimeException("Failed to save to database: " + profile, e);
        }
    }

    @Transactional
    public void delete() {
        User user = getAuthenticatedProfile();
        try {
            List<ConfirmationToken> token = confirmationTokenRepository.findAllByUser(user);
            confirmationTokenRepository.deleteAll(token);
            repository.delete(user);
            log.info("profile deleted " + user.getEmail());
        } catch (DataAccessException e) {
            log.trace("profile not found " + e.getMessage() + "cause" + e.getCause());
            throw new EntityNotFoundException("profile not found " + e.getMessage());
        }
    }
}
