package com.example.auth.api;

import com.example.auth.data.dto.UserDTO;
import com.example.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/profile")
@RestController
@Tag(name = "profile controller", description = "Profile management")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get user DTO")
    public UserDTO get() {
        return userService.getDTO();
    }

    @PatchMapping("/name")
    @Operation(summary = "Update user name")
    public UserDTO rename(@RequestBody List<String> name) {
        return userService.rename(name);
    }

    @DeleteMapping
    @Operation(summary = "Delete user profile")
    public void delete() {
        userService.delete();
    }
}