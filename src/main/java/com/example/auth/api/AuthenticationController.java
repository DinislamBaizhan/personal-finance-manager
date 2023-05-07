package com.example.auth.api;

import com.example.auth.data.dto.RegisterDTO;
import com.example.auth.data.entity.User;
import com.example.auth.data.request.AuthenticationRequest;
import com.example.auth.data.response.AuthenticationResponse;
import com.example.auth.service.AuthenticationService;
import com.example.auth.service.DecodedToken;
import com.example.auth.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "authentication controller", description = "Authentication management")
public class AuthenticationController {
    private final AuthenticationService service;
    private final UserService userService;

    public AuthenticationController(AuthenticationService service,
                                    UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @SneakyThrows
    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<String> register(
            @RequestBody @Valid RegisterDTO request
    ) {
        service.register(request);
        return ResponseEntity.ok("a message was sent to the email: "
                + request.getEmail() + " to verify the account");
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate a user")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) throws Exception {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify user e-mail")
    public String verifyEmail(@RequestParam("token") String token) {
        return service.confirmToken(token);
    }

    @PostMapping("/again")
    @Operation(summary = "Request verification e-mail one more time")
    public ResponseEntity<String> sendAgain(@RequestBody RegisterDTO registerDTO) throws Exception {


        User profile = userService.findByEmail(registerDTO.getEmail());

        RegisterDTO register = new RegisterDTO(
                profile.getFirstname(),
                profile.getLastname(),
                profile.getEmail(),
                profile.getPassword()
        );

        service.register(register);
        return ResponseEntity.ok("Email sent to confirm profile again");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Request for password reset e-mail")
    public ResponseEntity<String> passwordReset(@RequestBody String email) throws JsonProcessingException {
        service.resetPassword(email);
        return ResponseEntity.ok("check your email to reset your password");
    }

    @PatchMapping("/reset-password")
    @Operation(summary = "Update user profile with a new password")
    public ResponseEntity<String> passwordReset(
            @RequestParam("token") String token,
            @RequestBody String password
    ) throws Exception {

        DecodedToken decodedToken = DecodedToken.getDecoded(token);
        String email = decodedToken.sub;
        User profile = userService.findByEmail(email);

        service.updatePassword(profile, password);

        return ResponseEntity.ok("your password has been successfully updated");
    }
}