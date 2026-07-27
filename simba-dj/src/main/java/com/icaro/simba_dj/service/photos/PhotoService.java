package com.icaro.simba_dj.service.photos;

import com.icaro.simba_dj.dto.request.photos.PhotoRequest;
import com.icaro.simba_dj.dto.response.photos.PhotoResponse;
import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.exception.ResourceNotFoundException;
import com.icaro.simba_dj.mappers.photos.PhotosMapper;
import com.icaro.simba_dj.model.photos.Photo;
import com.icaro.simba_dj.model.profiles.Profile;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.repository.photos.PhotoRepository;
import com.icaro.simba_dj.repository.profiles.ProfileRepository;
import com.icaro.simba_dj.service.image.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PhotosMapper photosMapper;
    @Autowired
    private ImageStorageService imageStorageService;

    @Transactional
    public PhotoResponse createPhoto(PhotoRequest photoRequest, User user,
                                     MultipartFile image) throws IOException {

        Profile profile = profileRepository.findByUserUserId(user.getUserId())
                .orElseThrow(()-> new ResourceNotFoundException("Profile not found"));

        String imageUrl = imageStorageService.saveImage(image);

        Photo photo = photosMapper.toEntity(photoRequest);
        photo.setUrl(imageUrl);
        photo.setProfile(profile);
        photoRepository.save(photo);
        return photosMapper.toResponse(photo);
    }

    @Transactional
    public PhotoResponse updatePhoto(PhotoRequest photoRequest, User user
                                     ,UUID photoId,
                                     MultipartFile image) throws IOException {

        Profile profile = profileRepository.findByUserUserId(user.getUserId())
                .orElseThrow(()-> new ResourceNotFoundException("Profile not found"));

        Photo photo = photoRepository
                .findByPhotoIdAndProfileProfileIdAndPhotoStatus(photoId,profile.getProfileId(), ContentStatus.ACTIVE)
                .orElseThrow(()-> new ResourceNotFoundException("Photo not found"));
        if(image != null && !image.isEmpty()){
            String imageUrl = imageStorageService.saveImage(image);
            photo.setUrl(imageUrl);
        }


        if(photoRequest.displayOrder()!=null){
            photo.setDisplayOrder(photoRequest.displayOrder());
        }
        return  photosMapper.toResponse(photoRepository.save(photo));
    }

    @Transactional
    public void deletePhoto(UUID photoId, User user) {
        Profile profile = profileRepository.findByUserUserId(user.getUserId())
                .orElseThrow(()-> new ResourceNotFoundException("Profile not found"));

        Photo photo = photoRepository
                .findByPhotoIdAndProfileProfileIdAndPhotoStatus(photoId,profile.getProfileId(), ContentStatus.ACTIVE)
                .orElseThrow(()-> new ResourceNotFoundException("Photo not found"));

        photo.setPhotoStatus(ContentStatus.INACTIVE);
        photoRepository.save(photo);
    }

    @Transactional
    public List<PhotoResponse> getPhoto(User user){
        Profile profile = profileRepository.findByUserUserId(user.getUserId())
                .orElseThrow(()-> new ResourceNotFoundException("Profile not found"));
        return photoRepository.findByProfileProfileIdAndPhotoStatus(profile.getProfileId(),ContentStatus.ACTIVE)
                .stream()
                .map(photosMapper::toResponse)
                .toList();
    }
}
