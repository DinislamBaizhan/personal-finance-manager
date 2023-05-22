package com.example.finance.api;

import com.example.finance.data.dto.RegisterDTO;
import com.example.finance.data.request.AuthenticationRequest;
import com.example.finance.data.response.AuthenticationResponse;
import com.example.finance.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "authentication controller", description = "Authentication management")
public class AuthenticationController {
    private final AuthenticationService service;

    @SneakyThrows
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Register new user")
    @ApiResponse(responseCode = "200", description = "A message was sent to the email to verify the account", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    public ResponseEntity<String> register(
            @ModelAttribute @Valid RegisterDTO request
    ) {
        service.register(request);
        return ResponseEntity.ok("a message was sent to the email: "
                + request.getEmail() + " to verify the account");
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate a user")
    @ApiResponse(responseCode = "200", description = "User authentication successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class)))
    @ApiResponse(responseCode = "401", description = "User authentication failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify user e-mail")
    @ApiResponse(responseCode = "200", description = "Email verified successfully", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "400", description = "Invalid token", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
    public ResponseEntity<String> verifyEmail(@Parameter(description = "The verification token.",
            required = true) @RequestParam("token") String token) {
        service.confirmToken(token);
        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/again")
    @Operation(summary = "Request verification e-mail one more time")
    @ApiResponse(responseCode = "200", description = "The request was successful.")
    public ResponseEntity<String> sendAgain(@RequestParam("email") String email) {

        RegisterDTO register = new RegisterDTO();
        register.setEmail(email);

        service.register(register);
        return ResponseEntity.ok("Email sent to confirm profile again");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Request for password reset e-mail")
    @ApiResponse(responseCode = "200", description = "The request was successful.")
    public ResponseEntity<String> passwordReset(@Parameter(description = "The user's email address.",
            required = true) @RequestParam("email") String email) {
        service.resetPassword(email);
        return ResponseEntity.ok("check your email to reset your password");
    }

    @PatchMapping("/reset-password")
    @Operation(summary = "Update user profile with a new password")
    public ResponseEntity<String> passwordReset(@Parameter(description = "Authentication token", required = true)
                                                @RequestParam("token") String token,
                                                @RequestParam("password") String password
    ) throws Exception {
        service.updatePassword(password, token);
        return ResponseEntity.ok("your password has been successfully updated");
    }
}