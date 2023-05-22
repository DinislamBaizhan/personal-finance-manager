package com.example.finance.data.request;

import com.example.finance.data.Password;
import jakarta.validation.constraints.Email;

public class AuthenticationRequest {
    @Email
    private String email;
    @Password
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
