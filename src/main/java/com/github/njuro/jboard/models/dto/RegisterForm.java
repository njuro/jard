package com.github.njuro.jboard.models.dto;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import static com.github.njuro.jboard.helpers.Constants.*;

/**
 * Data transfer object for "create new user" form
 */
@Data
public class RegisterForm {

    @Size(min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH, message = "Username must have between " + MIN_USERNAME_LENGTH + " and " + MAX_USERNAME_LENGTH + " chars")
    private String username;

    @Size(min = MIN_PASSWORD_LENGTH, message = "Password too short (must be at least " + MIN_PASSWORD_LENGTH + ") chars")
    private String password;

    private String passwordRepeated;

    @Email(message = "Invalid email")
    private String email;

    private String registrationIp;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordMatching() {
        return password.equals(passwordRepeated);
    }

}
