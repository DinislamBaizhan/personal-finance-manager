package com.example.auth.service;

import com.example.auth.data.dto.RegisterDTO;
import com.example.auth.data.dto.UserDTO;
import com.example.auth.data.entity.User;
import com.example.auth.data.enums.Role;
import com.example.auth.exception.DataNotFound;
import com.example.auth.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;


    public User getAuthenticatedProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        return repository.findByEmail(email).orElseThrow(
                () -> new DataNotFound("profile not found")
        );
    }

    public UserDTO getDTO() {
        User user = getAuthenticatedProfile();
        return new UserDTO(
                user.getFirstname(),
                user.getLastname(),
                user.getEmail()
        );
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> {
                    log.error("user not found %s", email);
                    return new DataNotFound("profile not found");
                }
        );
    }

    public User updPassword(User profile, String password) throws Exception {
        profile.setPassword(passwordEncoder.encode(password));
        try {
            return repository.save(profile);
        } catch (Exception e) {
            throw new RuntimeException("fail to update password " + e.getCause());
        }
    }

    public UserDTO rename(List<String> name) {
        User user = getAuthenticatedProfile();
        user.setFirstname(name.get(0));
        user.setLastname(name.get(1));
        log.info("rename: firstname - %s", name.get(0), "lastname - %s", name.get(1));
        repository.save(user);
        return getDTO();
    }

    public UserDTO addIcon(String link) throws JsonProcessingException {
//        UserDTO DTO = objectMapper.readValue(link, UserDTO.class);
        User user = getAuthenticatedProfile();
        save(user);
        log.info("add icon");
        return getDTO();
    }

    public UserDTO addLanguage(String language) throws JsonProcessingException {
//        UserDTO userDTO = objectMapper.readValue(language, UserDTO.class);
        User user = getAuthenticatedProfile();
        save(user);
        return getDTO();
    }

    public UserDTO addColor(String color) throws JsonProcessingException {
//        UserDTO userDTO = objectMapper.readValue(color, UserDTO.class);
        User user = getAuthenticatedProfile();
        save(user);
        return getDTO();
    }

    public void delete() {
        User user = getAuthenticatedProfile();
        try {
            repository.delete(user);
            log.info("profile deleted " + user.getEmail());
        } catch (DataAccessException e) {
            log.trace("profile not found " + e.getMessage() + "cause" + e.getCause());
            throw new DataNotFound("profile not found " + e.getMessage());
        }
    }

    public User saveNewUser(RegisterDTO request) {
        User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        user.setEnabled(false);
        try {
            return repository.save(user);
        } catch (Exception ex) {
            throw new RuntimeException("can not save " + user.getEmail());
        }
    }


    public User save(User profile) {
        try {
            return repository.save(profile);
        } catch (DataAccessException e) {
            log.error("fail save to database %s", profile);
            throw new RuntimeException("fail save to database " + profile);
        }
    }
}
