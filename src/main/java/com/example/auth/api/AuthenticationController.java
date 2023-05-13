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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @ApiResponse(responseCode = "200", description = "A message was sent to the email to verify the account", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    public ResponseEntity<String> register(
            @RequestBody @Valid RegisterDTO request
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
    ) throws Exception {
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
    @ApiResponse(responseCode = "200", description = "The request was successful.")
    public ResponseEntity<String> passwordReset(@Parameter(description = "The user's email address.",
            required = true) @RequestBody String email) throws JsonProcessingException {
        service.resetPassword(email);
        return ResponseEntity.ok("check your email to reset your password");
    }

    @PatchMapping("/reset-password")
    @Operation(summary = "Update user profile with a new password")
    public ResponseEntity<String> passwordReset(@Parameter(description = "Authentication token" ,required = true)
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