package com.icaro.simba_dj.service.songs;

import com.icaro.simba_dj.dto.request.songs.SongRequest;
import com.icaro.simba_dj.dto.response.songs.SongResponse;
import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.exception.ResourceNotFoundException;
import com.icaro.simba_dj.mappers.songs.SongsMapper;
import com.icaro.simba_dj.model.plataformLink.SongPlatformLink;
import com.icaro.simba_dj.model.profiles.Profile;
import com.icaro.simba_dj.model.songs.Song;
import com.icaro.simba_dj.repository.profiles.ProfileRepository;
import com.icaro.simba_dj.repository.songs.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;
    @Autowired
    private SongsMapper songsMapper;
    @Autowired
    private ProfileRepository profileRepository;

    @Transactional
    public SongResponse createSong(SongRequest songRequest, UUID userId) {
        Profile profile = profileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        Song song = songsMapper.toEntity(songRequest);
        song.setProfile(profile);

        List<SongPlatformLink> links = songRequest.platformLinks().stream()
                .map(dto -> {
                    SongPlatformLink link = new SongPlatformLink();
                    link.setPlatform(dto.platform());
                    link.setExternalLink(dto.externalLink());
                    link.setSong(song);
                    return link;
                })
                .toList();

        song.setPlatformLinks(links);
        return SongsMapper.toResponse(songRepository.save(song));
    }

    @Transactional
    public SongResponse updateSong(SongRequest songRequest, UUID userId, UUID songId) {
        Profile profile = profileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        Song song = songRepository
                .findBySongIdAndProfileProfileIdAndSongStatus(songId, profile.getProfileId(), ContentStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        if(songRequest.coverUrl() != null){
            song.setCoverUrl(songRequest.coverUrl());
        }
        if(songRequest.title() != null){
            song.setTitle(songRequest.title());
        }
        if(songRequest.displayOrder() != null){
            song.setDisplayOrder(songRequest.displayOrder());
        }
        if (songRequest.platformLinks() != null && !songRequest.platformLinks().isEmpty()) {
            song.getPlatformLinks().clear();
            List<SongPlatformLink> newLinks = songRequest.platformLinks().stream()
                    .map(dto -> {
                        SongPlatformLink link = new SongPlatformLink();
                        link.setPlatform(dto.platform());
                        link.setExternalLink(dto.externalLink());
                        link.setSong(song);
                        return link;
                    })
                    .toList();
            song.getPlatformLinks().addAll(newLinks);
        }

        return SongsMapper.toResponse(song);
    }

    @Transactional
    public List<SongResponse> getSong(UUID userId){
        Profile profile = profileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return songRepository.findByProfileProfileIdAndSongStatus(profile.getProfileId(),ContentStatus.ACTIVE)
                .stream()
                .map(SongsMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteSong(UUID userId, UUID songId) {
        Profile profile = profileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        Song song = songRepository
                .findBySongIdAndProfileProfileIdAndSongStatus(songId,profile.getProfileId(),ContentStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        song.setSongStatus(ContentStatus.INACTIVE);
        songRepository.save(song);
    }

}
