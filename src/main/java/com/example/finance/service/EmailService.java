package com.example.finance.service;

import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    Logger logger = LogManager.getLogger();

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    @Transactional
    public void sendEmail(String email, String subject, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        try {
            helper.setFrom("personal_finance@hackaton.com");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(message);
            logger.info("send message to + %s", email);
        } catch (jakarta.mail.MessagingException e) {
            logger.error(MessageFormat.format("Failed to send email for: {0} {1}", email, e));
            throw new IllegalArgumentException("Failed to send email for: " + email);
        }
    }
}

