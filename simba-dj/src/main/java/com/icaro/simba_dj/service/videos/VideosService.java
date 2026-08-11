package com.icaro.simba_dj.service.videos;

import com.icaro.simba_dj.dto.request.videos.VideoRequest;
import com.icaro.simba_dj.dto.response.videos.VideoResponse;
import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.exception.ResourceNotFoundException;
import com.icaro.simba_dj.mappers.videos.VideoMapper;
import com.icaro.simba_dj.model.profiles.Profile;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.model.videos.Video;
import com.icaro.simba_dj.repository.profiles.ProfileRepository;
import com.icaro.simba_dj.repository.videos.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VideosService {
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private VideoMapper videoMapper;

    @Transactional
    public VideoResponse createVideo(VideoRequest videoRequest, User user) {
        Profile profile = profileRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        Video video = videoMapper.toEntity(videoRequest);
        video.setProfile(profile);
        videoRepository.save(video);
        return videoMapper.toResponse(video);
    }

    @Transactional
    public VideoResponse updateVideo(VideoRequest videoRequest, User user, UUID videoId) {
        Profile profile = profileRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        Video video = videoRepository
                .findByProfileProfileIdAndVideoStatusAndVideoId(profile.getProfileId(), ContentStatus.ACTIVE, videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found"));

        if(videoRequest.title() != null){
            video.setTitle(videoRequest.title());
        }
        if(videoRequest.url() != null){
            video.setUrl(videoRequest.url());
        }
        if(videoRequest.displayOrder() != null){
            video.setDisplayOrder(videoRequest.displayOrder());
        }
        if(videoRequest.thumbnailUrl() != null){
            video.setThumbnailUrl(videoRequest.thumbnailUrl());
        }

        return videoMapper.toResponse(video);
    }

    @Transactional
    public void deleteVideo(UUID videoId, User user) {
        Profile profile = profileRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        Video video = videoRepository
                .findByProfileProfileIdAndVideoStatusAndVideoId(profile.getProfileId(), ContentStatus.ACTIVE, videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found"));

        video.setVideoStatus(ContentStatus.INACTIVE);
        videoRepository.save(video);
    }

    @Transactional
    public List<VideoResponse> listVideos(User user){
        Profile profile = profileRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return videoRepository.findByProfileProfileIdAndVideoStatus(profile.getProfileId(), ContentStatus.ACTIVE)
                .stream()
                .map(videoMapper::toResponse)
                .toList();
    }
}
