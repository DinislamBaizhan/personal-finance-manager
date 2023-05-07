package com.example.habit_tracker.api;

import com.example.habit_tracker.data.dto.RegisterDTO;
import com.example.habit_tracker.data.enums.Role;
import com.example.habit_tracker.data.request.AuthenticationRequest;
import com.example.habit_tracker.data.response.AuthenticationResponse;
import com.example.habit_tracker.repository.TokenRepository;
import com.example.habit_tracker.service.AuthenticationService;
import com.example.habit_tracker.service.JwtService;
import com.example.habit_tracker.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @MockBean
    private AuthenticationService service;
    @MockBean
    private UserService userService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private TokenRepository tokenRepository;
    @Autowired
    private MockMvc mockMvc;

    private Profile mockProfile = new Profile("Michael", "Bane",
            "michael@gmail.com", "Qwertyui1_", Role.USER, false);
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void registerTestCorrect() throws Exception {

        RegisterDTO mockRegisterDTO = new RegisterDTO(
                mockProfile.getFirstname(),
                mockProfile.getLastname(),
                mockProfile.getEmail(),
                mockProfile.getPassword());

        String jsonContent = mapper.writeValueAsString(mockRegisterDTO);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(content().string("Email sent to verify profile"));
    }

    @Test
    void registerTestInvalidInput() throws Exception {

        RegisterDTO mockRegisterDTO = new RegisterDTO(
                mockProfile.getFirstname(),
                mockProfile.getLastname(),
                "badema@il@com.ru.org",
                "unsecurepass");

        String jsonContent = mapper.writeValueAsString(mockRegisterDTO);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void authenticateTest() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail(mockProfile.getEmail());
        request.setPassword(mockProfile.getPassword());
        String jsonRequest = mapper.writeValueAsString(request);

        JwtService jwtService = new JwtService(tokenRepository);

        String mockToken = jwtService.generateToken(mockProfile, 1000 * 60 * 30);
        AuthenticationResponse mockResponse = new AuthenticationResponse(mockToken);
        String jsonResponse = mapper.writeValueAsString(mockResponse);

        when(service.authenticate(any())).thenReturn(new AuthenticationResponse(mockToken));

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

    }
//
//    @Test
//    void verifyEmail() {
//    }
//
//    @Test
//    void sendAgain() {
//    }
//
//    @Test
//    void passwordReset() {
//    }
//
//    @Test
//    void testPasswordReset() {
//    }
}