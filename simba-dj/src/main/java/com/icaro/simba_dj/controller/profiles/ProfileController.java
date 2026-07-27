package com.icaro.simba_dj.controller.profiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icaro.simba_dj.dto.request.profiles.ProfileRequest;
import com.icaro.simba_dj.dto.request.profiles.ProfileUpdateRequest;
import com.icaro.simba_dj.dto.response.profiles.ProfileResponse;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.service.profiles.ProfileService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private ProfileService profileService;
    @Autowired
    private Validator validator;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProfile(@RequestPart("profile") String profileJson,
                                           @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                                           @AuthenticationPrincipal(expression = "user") User user) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ProfileRequest profileRequest = mapper.readValue(profileJson, ProfileRequest.class);
        validateProfileRequest(profileRequest);
        ProfileResponse profileResponse = profileService.createProfile(profileRequest, profileImage, user);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{profileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(@RequestPart(value = "profile", required = false) String profileJson,
                                           @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                                           @AuthenticationPrincipal(expression = "user") User user,
                                           @PathVariable UUID profileId) throws IOException{

        ProfileUpdateRequest profileRequest = null;

        if (profileJson != null && !profileJson.isBlank()) {
            ObjectMapper mapper = new ObjectMapper();
            profileRequest = mapper.readValue(profileJson, ProfileUpdateRequest.class);
        }

        ProfileResponse profileResponse = profileService.updateProfile(profileId, profileRequest, user.getUserId(), profileImage);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }


    @GetMapping("/me")
    public  ResponseEntity<?> getProfileByUserId( @AuthenticationPrincipal(expression = "user") User user){

        ProfileResponse profileResponse = profileService.getProfile(user.getUserId());
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    private void validateProfileRequest(ProfileRequest profileRequest) {
        Set<ConstraintViolation<ProfileRequest>> violations = validator.validate(profileRequest);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

}
