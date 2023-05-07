package com.example.auth.data.dto;

import com.example.auth.data.Password;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    private String firstname;
    private String lastname;
    @Email
    private String email;
    @Password
    private String password;
}
