package com.icaro.simba_dj.mappers.photos;

import com.icaro.simba_dj.dto.request.photos.PhotoRequest;
import com.icaro.simba_dj.dto.response.photos.PhotoResponse;
import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.model.photos.Photo;
import org.springframework.stereotype.Component;

@Component
public class PhotosMapper {
    public Photo toEntity(PhotoRequest photoRequest) {
        Photo photo = new Photo();
        photo.setDisplayOrder(photoRequest.displayOrder());
        photo.setPhotoStatus(ContentStatus.ACTIVE);
        return photo;
    }

    public PhotoResponse toResponse(Photo photo) {
        return new PhotoResponse(
                photo.getPhotoId(),
                photo.getUrl(),
                photo.getDisplayOrder(),
                photo.getCreatedAt(),
                photo.getPhotoStatus()
        );
    }
}
