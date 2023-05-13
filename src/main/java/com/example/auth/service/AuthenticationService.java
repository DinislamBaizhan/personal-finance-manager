package com.example.auth.service;

import com.example.auth.data.dto.MailDetails;
import com.example.auth.data.dto.RegisterDTO;
import com.example.auth.data.entity.ConfirmationToken;
import com.example.auth.data.entity.Token;
import com.example.auth.data.entity.User;
import com.example.auth.data.enums.TokenType;
import com.example.auth.data.request.AuthenticationRequest;
import com.example.auth.data.response.AuthenticationResponse;
import com.example.auth.repository.TokenRepository;
import com.example.auth.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerErrorException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final ApplicationEventPublisher eventPublisher;
    Logger logger = LogManager.getLogger();

    @Transactional
    public void register(@Valid RegisterDTO registerDTO) {
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

            String link = "http://localhost:8080/api/v1/auth/verify-email?token=" + confirmationToken.getToken();
            MailDetails mailDetails = new MailDetails(
                    savedUser.getEmail(),
                    savedConfirmationToken.getToken(),
                    buildEmail(savedUser.getFirstname(), link, confirmationToken.getExpiresAt())
            );
            eventPublisher.publishEvent(mailDetails);
        } else if (userExists.get().isEnabled()) {
            throw new UsernameNotFoundException("User with " + userExists.get().getEmail() + " is already exist");
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
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

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
            throw new BadCredentialsException("verify email");
        }
    }

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
            throw new ServerErrorException("Failed to save token for user with id " + profile.getId(), ex);
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
            throw new ServerErrorException("Failed to save all tokens", e.getCause());
        }
    }

    @Transactional
    public void confirmToken(String token) {
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
    }

    private String buildEmail(String name, String link, LocalDateTime expiresAt) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "<h1>Dear " + name + ",</h1>\n" +
                "<p>Congratulations! You have successfully registered on [Website Name]. We're thrilled to have you join our community. Before you get started, we kindly ask you to confirm your email address to ensure the security and reliability of your account.</p>\n" +
                "<p>To confirm your email address and activate your account, please follow the steps below:</p>\n" +
                "<ol>\n" +
                "<li>Click on the following link or copy and paste it into your web browser:<br>\n" +
                "<a href=\"" + link + "\">" + link + "</a></li>\n" +
                "<li>You will be redirected to a confirmation page on our website.</li>\n" +
                "<li>Once you land on the confirmation page, your email address will be verified, and your account will be activated.</li>\n" +
                "</ol>\n" +
                "<p>Please note that the confirmation link will expire in " + expiresAt + ". If you do not confirm your email address within this time frame, you may need to register again.</p>\n" +
                "<p>If you did not register on [Website Name] or believe this email was sent to you by mistake, please disregard it, and no further action is required.</p>\n" +
                "<p>If you encounter any issues during the registration process or have any questions, please feel free to reach out to our support team at [Support Email Address]. We're here to assist you.</p>\n" +
                "<p>Thank you for choosing [Website Name]. We look forward to providing you with an amazing experience!</p>\n" +
                "<p>Best regards,</p>\n" +
                "<p>[Your Name]<br>\n" +
                "[Your Position/Role]<br>\n" +
                "[Website Name]</p>\n" +
                "</div></div>";
    }
}

