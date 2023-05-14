package com.example.auth.utils;

import com.example.auth.data.dto.MailDetails;
import com.example.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NewUserNotifier {
    //private static final String VERIFY_MAIL_URI = "http://localhost:8080/api/v1/auth/verify-email?token=";
    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserCreate(MailDetails mailDetails) {
        emailService.sendEmail(
                mailDetails.getEmail(),
                "Confirm email",
                mailDetails.getMessage()
        );
    }
}
