package com.example.habit_tracker.service;

import com.example.habit_tracker.data.dto.ProfileDTO;
import com.example.habit_tracker.data.dto.RegisterDTO;
import com.example.habit_tracker.data.entity.User;
import com.example.habit_tracker.data.enums.Role;
import com.example.habit_tracker.exception.DataNotFound;
import com.example.habit_tracker.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    Logger logger = LogManager.getLogger();

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, ObjectMapper objectMapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }


    public User getAuthenticatedProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        return repository.findByEmail(email).orElseThrow(
                () -> new DataNotFound("profile not found")
        );
    }


//    public User createUser(@Valid RegisterDTO request) {
//
//        Optional<User> profile = repository.getProfileByEmail(request.getEmail());
//
//        if (profile.isPresent() && Boolean.TRUE.equals(profile.get().getWaitingForVerification())) {
//            return profile.get();
//        } else if (profile.isPresent() && Boolean.TRUE.equals(!profile.get().getWaitingForVerification())) {
//            logger.error("Profile already registered %s", request.getEmail());
//            throw new DuplicateKey("Profile already registered");
//        }
//        try {
//            User newProfile = new User(
//                    request.getFirstname(),
//                    request.getLastname(),
//                    request.getEmail(),
//                    passwordEncoder.encode(request.getPassword()),
//                    Role.USER,
//                    true
//            );
//
//            return repository.save(newProfile);
//        } catch (DataAccessException e) {
//            logger.trace("Falideld save token %s %s", e, e.getCause());
//            throw new RuntimeException("Failed to create new profile", e.getCause());
//        }
//    }

//    public void updateVerify(User profile) throws Exception {
//        try {
//            profile.setEnabled(true);
//            repository.save(profile);
//        } catch (Exception e) {
//            logger.error("error verify profile %s %s", e.getMessage(), e.getCause());
//            throw new Exception(e.getMessage(), e.getCause());
//        }
//    }

    public ProfileDTO getDTO() {
        User profile = getAuthenticatedProfile();
        return new ProfileDTO(
                profile.getFirstname(),
                profile.getLastname(),
                profile.getEmail()
        );
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> {
                    logger.error("user not found %s", email);
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

    public ProfileDTO rename(List<String> name) {
        User profile = getAuthenticatedProfile();
        profile.setFirstname(name.get(0));
        profile.setLastname(name.get(1));
        logger.info("rename: firstname - %s", name.get(0), "lastname - %s", name.get(1));
        repository.save(profile);
        return getDTO();
    }

    public ProfileDTO addIcon(String link) throws JsonProcessingException {
        ProfileDTO pDTO = objectMapper.readValue(link, ProfileDTO.class);
        User user = getAuthenticatedProfile();
        save(user);
        logger.info("add icon");
        return getDTO();
    }

    public ProfileDTO addLanguage(String language) throws JsonProcessingException {
        ProfileDTO profileDTO = objectMapper.readValue(language, ProfileDTO.class);
        User user = getAuthenticatedProfile();
        save(user);
        return getDTO();
    }

    public ProfileDTO addColor(String color) throws JsonProcessingException {
        ProfileDTO profileDTO = objectMapper.readValue(color, ProfileDTO.class);
        User user = getAuthenticatedProfile();
        save(user);
        return getDTO();
    }

    public void delete() {
        User user = getAuthenticatedProfile();
        try {
            repository.delete(user);
            logger.info("profile deleted " + user.getEmail());
        } catch (DataAccessException e) {
            logger.trace("profile not found " + e.getMessage() + "cause" + e.getCause());
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
            logger.error("fail save to database %s", profile);
            throw new RuntimeException("fail save to database " + profile);
        }
    }
}
