package com.icaro.simba_dj.dto.request.token;

import com.icaro.simba_dj.enums.Role;

import java.util.UUID;

public record TokenRequest(
    String userEmail,
    UUID UserId,
    Role role
)
{
    public TokenRequest(String userEmail, UUID UserId, Role role) {
        this.userEmail = userEmail;
        this.UserId = UserId;
        this.role = role;
    }
}
