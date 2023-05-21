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
import com.example.auth.utils.DecodedToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerErrorException;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void register(@Valid RegisterDTO registerDTO) throws Exception {
        var userExists = userRepository
                .findByEmail(registerDTO.getEmail());

        if (userExists.isEmpty()) {
            User savedUser = userService.saveNewUser(registerDTO);
            messageForNewUser(savedUser);
        } else if (userExists.get().isEnabled()) {
            throw new DuplicateKeyException("User with " + userExists.get().getEmail() + " is already exist");
        } else {
            messageForNewUser(userExists.get());
        }
    }

    @Transactional
    public void messageForNewUser(User user) throws Exception {
        ConfirmationToken confirmationToken = confirmationTokenService
                .saveConfirmationToken(user);

        String link = "http://localhost:8080/api/v1/auth/verify-email?token=" + confirmationToken.getToken();
        MailDetails mailDetails = new MailDetails(
                user.getEmail(),
                confirmationToken.getToken(),
                buildEmail(user.getFirstname(), link, confirmationToken.getExpiresAt())
        );
        eventPublisher.publishEvent(mailDetails);
    }

    @Transactional
    public void resetPassword(String email) {
        int tokenExpiredDate = 1000 * 60 * 30;
        User user = userService.findByEmail(email);

        String jwtToken = jwtService.generateToken(user, tokenExpiredDate);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        String link = "http://localhost:8080/api/v1/auth/reset-password?token=" + jwtToken;
        MailDetails mailDetails = new MailDetails(
                email,
                jwtToken,
                emailToResetPass(user.getFirstname(), link, LocalDateTime.now().plusMinutes(30))
        );
        eventPublisher.publishEvent(mailDetails);
    }

    @Transactional
    public void updatePassword(String password, String token) throws Exception {
        DecodedToken decodedToken = DecodedToken.getDecoded(token);
        String email = decodedToken.sub;
        User user = userService.findByEmail(email);

        int tokenExpiredDate = 1000 * 60 * 60 * 24;
        User updatedProfile = userService.updPassword(user, password);
        String jwtToken = jwtService.generateToken(updatedProfile, tokenExpiredDate);
        revokeAllUserTokens(user);
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
            log.info("Authentication");
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
            log.info("Token saved for user with id {}", profile.getId());
        } catch (DataAccessException ex) {
            log.error("Failed to save token for user with id {}", profile.getId(), ex);
            throw new ServerErrorException("Failed to save token for user with id " + profile.getId(), ex);
        }
    }

    public void revokeAllUserTokens(User profile) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(profile.getId());
        if (validUserTokens.isEmpty()) {
            log.info("tokens not found");
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        try {
            tokenRepository.saveAll(validUserTokens);
        } catch (Exception e) {
            log.error("Failed to save all tokens\", e.getCause()");
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

    private String emailToResetPass(String name, String link, LocalDateTime expiresAt) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "<h1>Dear " + name + ",</h1>\n" +
                "<p>To confirm your email address and activate your account, please follow the steps below:</p>\n" +
                "<ol>\n" +
                "<li>Click on the following link or copy and paste it into your web browser:<br>\n" +
                "<a href=\"" + link + "\">" + link + "</a></li>\n" +
                "<li>You will be redirected to a confirmation page on our website.</li>\n" +
                "<li>As soon as you go to the page, you will be prompted to enter a new email password.</li>\n" +
                "</ol>\n" +
                "<p>PPlease note that the password reset link will expire at " + expiresAt + " If you do not verify your email address within this time period, you may need to re-register..</p>\n" +
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

