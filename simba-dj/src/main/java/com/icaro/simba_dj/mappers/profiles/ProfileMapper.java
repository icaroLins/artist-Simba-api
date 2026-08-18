package com.icaro.simba_dj.mappers.profiles;

import com.icaro.simba_dj.dto.request.profiles.ProfileRequest;
import com.icaro.simba_dj.dto.response.profiles.ProfileResponse;
import com.icaro.simba_dj.dto.response.profiles.PublicProfileResponse;
import com.icaro.simba_dj.model.profiles.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {
    public Profile toEntity(ProfileRequest profileRequest){
        Profile profile = new Profile();
        profile.setProfileInstagramUrl(profileRequest.profileInstagramUrl());
        profile.setProfileNameArtist(profileRequest.profileNameArtist());
        return profile;
    }

    public ProfileResponse toResponse(Profile profile){
        ProfileResponse response = new ProfileResponse(profile.getProfileId(),
                profile.getProfileNameArtist(), profile.getProfileShortlink(), profile.getProfilePhotoUrl(),
                profile.getProfileInstagramUrl(), profile.getUser().getUserRole(),
                profile.getUser().getUserId(), profile.getUser().getUserEmail());
        return response;
    }

    public PublicProfileResponse toPublicResponse(Profile profile) {
        return new PublicProfileResponse(
                profile.getProfileShortlink(),
                profile.getProfileNameArtist(),
                profile.getProfilePhotoUrl(),
                profile.getProfileInstagramUrl()
        );
    }
}
