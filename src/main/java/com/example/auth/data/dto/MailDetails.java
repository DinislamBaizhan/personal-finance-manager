package com.example.auth.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailDetails {
    private String email;
    private String confirmToken;
    private String message;
}
