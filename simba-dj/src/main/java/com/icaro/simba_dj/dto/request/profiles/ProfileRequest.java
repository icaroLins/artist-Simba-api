package com.icaro.simba_dj.dto.request.profiles;

import jakarta.validation.constraints.NotBlank;

public record ProfileRequest(
        @NotBlank(message = "Artist name is required.")
        String profileNameArtist,

        @NotBlank(message = "Instagram URL is required.")
        String profileInstagramUrl
) {
}
