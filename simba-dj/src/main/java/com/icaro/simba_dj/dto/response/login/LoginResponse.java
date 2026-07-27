package com.icaro.simba_dj.dto.response.login;

import com.icaro.simba_dj.enums.Role;

import java.util.UUID;

public record LoginResponse(
        String token,
        String email,
        UUID userId,
        Role userRole
){}
