package com.icaro.simba_dj.repository.photos;

import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.model.photos.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    List<Photo> findByProfileProfileIdAndPhotoStatus(UUID profileProfileId, ContentStatus status);
    Optional<Photo> findByPhotoIdAndProfileProfileIdAndPhotoStatus(UUID photoId, UUID profileProfileId,  ContentStatus status);
}
