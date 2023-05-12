package com.example.auth.api;

import com.example.auth.data.dto.UserDTO;
import com.example.auth.data.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/user")
@RestController
@RequiredArgsConstructor
@Tag(name = "profile controller", description = "user management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

//    public ProfileController(UserService userService) {
//        this.userService = userService;
//    }

    @GetMapping
    @Operation(summary = "Get user DTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user DTO"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public UserDTO get() {
        return userService.getDTO();
    }

    @GetMapping("1")
    @Operation(summary = "Get user entity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user entity"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName()).get();
    }

    @PatchMapping("/name")
    @Operation(summary = "Update user name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user name"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public UserDTO rename(@RequestBody List<String> name) {
        return userService.rename(name);
    }

    @DeleteMapping
    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void delete() {
        userService.delete();
    }
}