package com.icaro.simba_dj.dto.response.songs;

import com.icaro.simba_dj.dto.response.platformLink.PlatformLinkResponse;
import com.icaro.simba_dj.enums.ContentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SongResponse(
        UUID songId,
        String title,
        String shortlink,
        String coverUrl,
        Integer displayOrder,
        ContentStatus status,
        List<PlatformLinkResponse> platformLinks,
        LocalDateTime createdAt
) {
}
