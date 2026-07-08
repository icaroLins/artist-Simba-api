package com.icaro.simba_dj.dto.response.users;

import com.icaro.simba_dj.enums.Role;

public record UserResponse(
        String userEmail,
        Long userId,
        Role userRole
) {}
