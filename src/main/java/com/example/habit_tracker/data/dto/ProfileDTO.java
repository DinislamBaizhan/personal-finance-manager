package com.example.habit_tracker.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileDTO {
    private String firstname;
    private String lastname;
    private String email;

    public ProfileDTO(String firstname,
                      String lastname, String email
    ) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public ProfileDTO() {
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
