package com.icaro.simba_dj.mappers.videos;

import com.icaro.simba_dj.dto.request.videos.VideoRequest;
import com.icaro.simba_dj.dto.response.videos.VideoResponse;
import com.icaro.simba_dj.model.videos.Video;
import org.springframework.stereotype.Component;

@Component
public class VideoMapper {
    public Video toEntity(VideoRequest videoRequest) {
        Video video = new Video();
        video.setTitle(videoRequest.title());
        video.setUrl(videoRequest.url());
        video.setDisplayOrder(videoRequest.displayOrder());
        video.setThumbnailUrl(videoRequest.thumbnailUrl());
        return video;
    }

    public VideoResponse toResponse(Video video) {
        return new VideoResponse(
                video.getVideoId(),
                video.getTitle(),
                video.getUrl(),
                video.getThumbnailUrl(),
                video.getDisplayOrder(),
                video.getVideoStatus(),
                video.getCreatedAt(),
                video.getUpdatedAt()
        );
    }
}
