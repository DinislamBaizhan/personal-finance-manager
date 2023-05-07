package com.example.habit_tracker.service;

import com.example.habit_tracker.data.dto.RegisterDTO;
import com.example.habit_tracker.data.entity.ConfirmationToken;
import com.example.habit_tracker.data.entity.Token;
import com.example.habit_tracker.data.entity.User;
import com.example.habit_tracker.data.enums.TokenType;
import com.example.habit_tracker.data.request.AuthenticationRequest;
import com.example.habit_tracker.data.response.AuthenticationResponse;
import com.example.habit_tracker.repository.TokenRepository;
import com.example.habit_tracker.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    Logger logger = LogManager.getLogger();

    public AuthenticationService(UserService userService,
                                 TokenRepository tokenRepository,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager,
                                 EmailService emailService,
                                 ObjectMapper objectMapper, UserRepository userRepository, ConfirmationTokenService confirmationTokenService) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.confirmationTokenService = confirmationTokenService;
    }

    @Transactional
    public void register(@Valid RegisterDTO registerDTO) throws Exception {
        var userExists = userRepository
                .findByEmail(registerDTO.getEmail());

        if (userExists.isEmpty()) {

            User savedUser = userService.saveNewUser(registerDTO);

            String token = UUID.randomUUID().toString();

            ConfirmationToken confirmationToken = new ConfirmationToken(
                    token,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    savedUser
            );

            ConfirmationToken savedConfirmationToken = confirmationTokenService
                    .saveConfirmationToken(
                            confirmationToken);

            String link = "http://localhost:8080/api/v1/auth/verify-email?token=" + savedConfirmationToken.getToken();

            emailService.sendEmail(
                    savedUser.getEmail(),
                    link,
                    "Confirm your email");
        } else if (userExists.get().isEnabled()) {
            throw new Exception("User with " + userExists.get().getEmail() + " is already exist");
        } else {
            throw new UsernameNotFoundException("verify your jwt token");
        }

    }

    @Transactional
    public void resetPassword(String email) throws JsonProcessingException {

        AuthenticationRequest mappedEmail = objectMapper.readValue(email, AuthenticationRequest.class);

        int tokenExpiredDate = 1000 * 60 * 30;
        User profile = userService.findByEmail(mappedEmail.getEmail());

        String jwtToken = jwtService.generateToken(profile, tokenExpiredDate);
        revokeAllUserTokens(profile);
        saveUserToken(profile, jwtToken);

        String link = "http://localhost:8080/api/v1/auth/reset-password?token=" + jwtToken;
        emailService.sendEmail(profile.getEmail(), "Password Reset",
                "Click on this link to reset your password: " + link);
    }

    @Transactional
    public void updatePassword(User profile, String password) throws Exception {

        AuthenticationRequest mappedPassword = objectMapper.readValue(password, AuthenticationRequest.class);

        int tokenExpiredDate = 1000 * 60 * 60 * 24;
        User updatedProfile = userService.updPassword(profile, mappedPassword.getPassword());
        String jwtToken = jwtService.generateToken(updatedProfile, tokenExpiredDate);
        revokeAllUserTokens(profile);
        saveUserToken(updatedProfile, jwtToken);
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {

        User profile = userService.findByEmail(request.getEmail());
        if (profile.isEnabled()) {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            int tokenExpiredDate = 1000 * 60 * 60 * 24;
            var jwtToken = jwtService.generateToken(profile, tokenExpiredDate);
            revokeAllUserTokens(profile);
            saveUserToken(profile, jwtToken);
            logger.info("Authentication");
            return new AuthenticationResponse(
                    jwtToken
            );
        } else {
            throw new Exception("Verify email");
        }
    }

    //    @Transactional
    public void saveUserToken(User profile, String jwtToken) {
        var token = new Token(
                jwtToken,
                TokenType.BEARER,
                false,
                false, profile
        );
        try {
            tokenRepository.saveAndFlush(token);
            logger.info("Token saved for user with id {}", profile.getId());
        } catch (DataAccessException ex) {
            logger.error("Failed to save token for user with id {}", profile.getId(), ex);
            throw new RuntimeException("Failed to save token for user with id " + profile.getId(), ex);
        }
    }

    public void revokeAllUserTokens(User profile) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(profile.getId());
        if (validUserTokens.isEmpty()) {
            logger.info("tokens not found");
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        try {
            tokenRepository.saveAll(validUserTokens);
        } catch (Exception e) {
            logger.error("Failed to save all tokens\", e.getCause()");
            throw new RuntimeException("Failed to save all tokens", e.getCause());
        }
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);

        User user = userRepository.findByEmail(confirmationToken.getUser().getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("user not found by token "
                        + confirmationToken.getToken()));
        user.setEnabled(true);
        userRepository.save(user);

        return "confirmed";
    }
}

