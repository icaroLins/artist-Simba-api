package com.icaro.simba_dj.mappers.songs;

import com.icaro.simba_dj.dto.request.songs.SongRequest;
import com.icaro.simba_dj.dto.response.platformLink.PlatformLinkResponse;
import com.icaro.simba_dj.dto.response.songs.SongResponse;
import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.model.plataformLink.SongPlatformLink;
import com.icaro.simba_dj.model.songs.Song;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class SongsMapper {
    public Song toEntity(SongRequest request){
        Song song = new Song();
        song.setCoverUrl(request.coverUrl());
        song.setTitle(request.title());
        song.setDisplayOrder(request.displayOrder());
        song.setSongStatus(ContentStatus.ACTIVE);

        List<SongPlatformLink> platformLinks = request.platformLinks().stream()
                .map(dto -> {
                    SongPlatformLink platformLink = new SongPlatformLink();
                    platformLink.setPlatform(dto.platform());
                    platformLink.setExternalLink(dto.externalLink());
                    platformLink.setSong(song);
                    return platformLink;
                }).toList();

        song.setPlatformLinks(platformLinks);

        return song;
    }

    public static SongResponse toResponse(Song song) {
        List<PlatformLinkResponse> links = song.getPlatformLinks().stream()
                .map(link -> new PlatformLinkResponse(
                        link.getPlataformLinkId(),
                        link.getPlatform(),
                        link.getExternalLink()
                ))
                .toList();

        return new SongResponse(
                song.getSongId(),
                song.getTitle(),
                song.getShortlink(),
                song.getCoverUrl(),
                song.getDisplayOrder(),
                song.getSongStatus(),
                links,
                song.getCreatedAt()
        );
    }
}
