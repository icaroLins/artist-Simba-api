package com.icaro.simba_dj.dto.request.profiles;

import jakarta.validation.constraints.NotBlank;

public record ProfileUpdateRequest(
        String profileNameArtist,
        String profileInstagramUrl
) {
}
