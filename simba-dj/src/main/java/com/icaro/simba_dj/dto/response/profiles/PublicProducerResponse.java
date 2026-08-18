package com.icaro.simba_dj.dto.response.profiles;

import com.icaro.simba_dj.dto.response.events.EventResponse;
import com.icaro.simba_dj.dto.response.photos.PhotoResponse;
import com.icaro.simba_dj.dto.response.songs.SongResponse;
import com.icaro.simba_dj.dto.response.videos.VideoResponse;

import java.util.List;

public record PublicProducerResponse(
        PublicProfileResponse profile,
        List<SongResponse> songs,
        List<PhotoResponse> photos,
        List<VideoResponse> videos,
        List<EventResponse> events
) {
}
