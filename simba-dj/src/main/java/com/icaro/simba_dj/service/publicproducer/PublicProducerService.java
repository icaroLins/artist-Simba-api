package com.icaro.simba_dj.service.publicproducer;

import com.icaro.simba_dj.dto.response.events.EventResponse;
import com.icaro.simba_dj.dto.response.photos.PhotoResponse;
import com.icaro.simba_dj.dto.response.profiles.PublicProducerResponse;
import com.icaro.simba_dj.dto.response.profiles.PublicProfileResponse;
import com.icaro.simba_dj.dto.response.songs.SongResponse;
import com.icaro.simba_dj.dto.response.videos.VideoResponse;
import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.exception.ResourceNotFoundException;
import com.icaro.simba_dj.mappers.events.EventMapper;
import com.icaro.simba_dj.mappers.photos.PhotosMapper;
import com.icaro.simba_dj.mappers.profiles.ProfileMapper;
import com.icaro.simba_dj.mappers.songs.SongsMapper;
import com.icaro.simba_dj.mappers.videos.VideoMapper;
import com.icaro.simba_dj.model.profiles.Profile;
import com.icaro.simba_dj.repository.events.EventRepository;
import com.icaro.simba_dj.repository.photos.PhotoRepository;
import com.icaro.simba_dj.repository.profiles.ProfileRepository;
import com.icaro.simba_dj.repository.songs.SongRepository;
import com.icaro.simba_dj.repository.videos.VideoRepository;
import com.icaro.simba_dj.service.shared.ShortlinkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PublicProducerService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileMapper profileMapper;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PhotosMapper photosMapper;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private EventMapper eventMapper;

    @Transactional(readOnly = true)
    public PublicProducerResponse getPublicProducer(String shortlink) {
        Profile profile = resolveProfile(shortlink);

        PublicProfileResponse publicProfile = profileMapper.toPublicResponse(profile);
        List<SongResponse> songs = songRepository
                .findByProfileProfileIdAndSongStatus(profile.getProfileId(), ContentStatus.ACTIVE)
                .stream()
                .map(SongsMapper::toResponse)
                .toList();
        List<PhotoResponse> photos = photoRepository
                .findByProfileProfileIdAndPhotoStatus(profile.getProfileId(), ContentStatus.ACTIVE)
                .stream()
                .map(photosMapper::toResponse)
                .toList();
        List<VideoResponse> videos = videoRepository
                .findByProfileProfileIdAndVideoStatus(profile.getProfileId(), ContentStatus.ACTIVE)
                .stream()
                .map(videoMapper::toResponse)
                .toList();
        List<EventResponse> events = eventRepository
                .findByProfileProfileIdAndEventStatus(profile.getProfileId(), ContentStatus.ACTIVE)
                .stream()
                .map(eventMapper::toResponse)
                .toList();

        return new PublicProducerResponse(publicProfile, songs, photos, videos, events);
    }

    private Profile resolveProfile(String shortlink) {
        String normalizedShortlink = ShortlinkUtil.normalize(shortlink);

        return profileRepository.findByProfileShortlink(normalizedShortlink)
                .orElseGet(() -> profileRepository.findAll().stream()
                        .filter(profile -> normalizedShortlink.equals(ShortlinkUtil.normalize(profile.getProfileShortlink()))
                                || normalizedShortlink.equals(ShortlinkUtil.normalize(profile.getProfileNameArtist())))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Producer not found")));
    }
}
