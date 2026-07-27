package com.icaro.simba_dj.dto.response.videos;

import com.icaro.simba_dj.enums.ContentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record VideoResponse(
        UUID videoId,
        String title,
        String url,
        String thumbnailUrl,
        Integer displayOrder,
        ContentStatus videoStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
