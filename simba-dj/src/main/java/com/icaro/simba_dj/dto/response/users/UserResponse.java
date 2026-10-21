package com.icaro.simba_dj.dto.response.users;

import com.icaro.simba_dj.enums.Role;

import java.util.UUID;

public record UserResponse(
        String userEmail,
        UUID userId,
        Role userRole
) {}
