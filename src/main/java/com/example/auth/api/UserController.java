package com.example.auth.api;

import com.example.auth.data.dto.UserDTO;
import com.example.auth.data.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

//    public ProfileController(UserService userService) {
//        this.userService = userService;
//    }

    @GetMapping
    @Operation(summary = "Get user DTO")
    public UserDTO get() {
        return userService.getDTO();
    }

    @GetMapping("1")
    @Operation(summary = "Get user entity")
    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName()).get();
    }

    @PatchMapping("/name")
    @Operation(summary = "Update user name")
    public UserDTO rename(@RequestBody List<String> name) {
        return userService.rename(name);
    }

    @DeleteMapping
    @Operation(summary = "Delete user")
    public void delete() {
        userService.delete();
    }
}