package com.example.auth.service;

import com.example.auth.data.entity.ConfirmationToken;
import com.example.auth.data.entity.User;
import com.example.auth.repository.ConfirmationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Transactional
    public ConfirmationToken saveConfirmationToken(User user) throws Exception {
        Optional<ConfirmationToken> token = confirmationTokenRepository.findByConfirmedIsFalseAndUserAndExpiresAtAfter(user, LocalDateTime.now());

        if (token.isPresent()) {
            throw new Exception("Your email link is still active, try again in 15 minutes");
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
}