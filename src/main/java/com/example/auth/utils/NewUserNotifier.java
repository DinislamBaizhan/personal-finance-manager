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
