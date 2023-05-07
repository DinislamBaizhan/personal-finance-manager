package com.example.habit_tracker.service;

import com.example.habit_tracker.data.enums.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DecodedTokenTest {
    private final int tokenExpiredDate = 1000 * 60 * 30;
    private String mockToken;
    private Profile mockProfile;
    @InjectMocks
    private JwtService jwtService;

    @BeforeAll
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockProfile = new Profile("Michael", "Bane", "michael@gmail.com",
                "Qwertyui1_", Role.USER, false);
        mockToken = jwtService.generateToken(mockProfile, tokenExpiredDate);
    }

    @Test
    void getDecodedTest() throws UnsupportedEncodingException, JsonProcessingException {
        DecodedToken result = DecodedToken.getDecoded(mockToken);
        assertThat(result.getSub()).isEqualTo(mockProfile.getEmail());
        assertThat(result.getIat()).isNotZero();
        assertThat(Long.parseLong(result.getExp())).isEqualTo(result.getIat() + tokenExpiredDate / 1000);
    }
}