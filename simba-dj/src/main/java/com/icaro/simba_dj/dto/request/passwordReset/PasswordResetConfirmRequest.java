package com.icaro.simba_dj.dto.request.passwordReset;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetConfirmRequest(
        @NotBlank(message = "Token is required.")
        String token,

        @NotBlank(message = "New password is required.")
        String newPassword
) {
}
