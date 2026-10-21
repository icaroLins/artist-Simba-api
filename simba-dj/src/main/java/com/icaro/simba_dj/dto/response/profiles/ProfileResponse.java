package com.icaro.simba_dj.dto.response.profiles;

import com.icaro.simba_dj.enums.Role;

import java.util.UUID;

public record ProfileResponse(
        UUID profileId,
        String profileNameArtist,
        String profileShortlink,
        String profilePhotoUrl,
        String profileInstagramUrl,
        Role userRole,
        UUID userId,
        String userEmail
) {}
