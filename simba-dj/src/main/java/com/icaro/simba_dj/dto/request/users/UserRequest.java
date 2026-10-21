package com.icaro.simba_dj.dto.request.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Email must be valid.")
        String userEmail,

        @NotBlank(message = "Password is required.")
        String userPassword_hash
) {}
