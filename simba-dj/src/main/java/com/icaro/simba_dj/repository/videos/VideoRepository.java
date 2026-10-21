package com.icaro.simba_dj.repository.videos;

import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.model.videos.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {
    List<Video> findByProfileProfileIdAndVideoStatus(UUID profileId, ContentStatus videoStatus);
    Optional<Video> findByProfileProfileIdAndVideoStatusAndVideoId(UUID profileId, ContentStatus videoStatus, UUID videoId);
}
