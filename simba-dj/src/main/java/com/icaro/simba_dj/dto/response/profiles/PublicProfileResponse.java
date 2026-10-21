package com.icaro.simba_dj.dto.response.profiles;

public record PublicProfileResponse(
        String profileShortlink,
        String profileNameArtist,
        String profilePhotoUrl,
        String profileInstagramUrl
) {
}
