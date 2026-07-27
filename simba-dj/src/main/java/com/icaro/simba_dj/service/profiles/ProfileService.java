package com.icaro.simba_dj.service.profiles;

import com.icaro.simba_dj.dto.request.profiles.ProfileRequest;
import com.icaro.simba_dj.dto.request.profiles.ProfileUpdateRequest;
import com.icaro.simba_dj.dto.response.profiles.ProfileResponse;
import com.icaro.simba_dj.exception.ResourceNotFoundException;
import com.icaro.simba_dj.mappers.profiles.ProfileMapper;
import com.icaro.simba_dj.model.profiles.Profile;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.repository.profiles.ProfileRepository;
import com.icaro.simba_dj.service.image.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.ConnectException;
import java.util.UUID;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileMapper profileMapper;
    @Autowired
    private ImageStorageService imageStorageService;

    public ProfileResponse createProfile(ProfileRequest profileRequest,
                                         MultipartFile photo, User user) throws IOException {
        Profile profile = profileMapper.toEntity(profileRequest);
        if (photo != null && !photo.isEmpty()) {
            String imageUrl = imageStorageService.saveImage(photo);
            profile.setProfilePhotoUrl(imageUrl);
        }

        profile.setUser(user);
        profileRepository.save(profile);

        return profileMapper.toResponse(profile);
    }

        public ProfileResponse updateProfile(UUID id,
                                             ProfileUpdateRequest profileRequest,
                                             UUID userId,
                                             MultipartFile photo) throws IOException {
            Profile profile = profileRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

            if(!profile.getUser().getUserId().equals(userId)){
                throw new ConnectException("Wrong user id");
            }

            if (profileRequest != null) {
                if(profileRequest.profileNameArtist() != null && !profileRequest.profileNameArtist().isEmpty()){
                    profile.setProfileNameArtist(profileRequest.profileNameArtist());
                }

                if(profileRequest.profileInstagramUrl() != null && !profileRequest.profileInstagramUrl().isEmpty()){
                    profile.setProfileInstagramUrl(profileRequest.profileInstagramUrl());
                }
            }

            if (photo != null && !photo.isEmpty()) {
                String imageUrl = imageStorageService.saveImage(photo);
                profile.setProfilePhotoUrl(imageUrl);
            }

            profileRepository.save(profile);
            return profileMapper.toResponse(profile);
        }

    public ProfileResponse getProfile(UUID userId){
        Profile profile = profileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(   "Profile not found"));

        return profileMapper.toResponse(profile);
    }

}
