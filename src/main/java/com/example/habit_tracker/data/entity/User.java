package com.example.habit_tracker.data.entity;

import com.example.habit_tracker.data.Password;
import com.example.habit_tracker.data.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(min = 2, max = 20)
    private String firstname;
    @NotNull
    @Size(min = 2, max = 20)
    private String lastname;
    @Email
    @NotNull
    @Column(unique = true, nullable = false)
    private String email;
    @NonNull
    @Password
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "enabled")
    private boolean isEnabled;
    @Enumerated(EnumType.STRING)
    private Role role;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Token> tokens = new ArrayList<>();

    public User(String firstname,
                String lastname,
                String email, @NonNull
                String password,
                Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
        isEnabled = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean bool) {
        isEnabled = bool;
    }
}
