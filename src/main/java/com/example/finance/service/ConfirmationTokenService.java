package com.example.finance.service;

import com.example.finance.data.entity.ConfirmationToken;
import com.example.finance.data.entity.User;
import com.example.finance.repository.ConfirmationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Transactional
    public ConfirmationToken saveConfirmationToken(User user) {
        Optional<ConfirmationToken> token = confirmationTokenRepository
                .findByConfirmedIsFalseAndUserAndExpiresAtAfter(user, LocalDateTime.now());

        if (token.isPresent()) {
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime tokenCreationTime = token.get().getCreatedAt();
            long minutesDifference = ChronoUnit.MINUTES.between(tokenCreationTime, currentTime);
            LocalDateTime differenceDateTime = currentTime.minusMinutes(minutesDifference);
            throw new IllegalArgumentException("Your email link is still active, try again in " + differenceDateTime + " minutes");
        } else {
            String randomUUID = UUID.randomUUID().toString();

            ConfirmationToken confirmationToken = new ConfirmationToken(
                    randomUUID,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    user
            );
            return confirmationTokenRepository.save(confirmationToken);
        }
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public void setConfirmedAt(String token) {
        confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }

    @Transactional
    public void delete() {
        confirmationTokenRepository.deleteAll();
    }
}