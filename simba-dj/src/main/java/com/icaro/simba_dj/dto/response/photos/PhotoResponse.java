package com.icaro.simba_dj.dto.response.photos;

import com.icaro.simba_dj.enums.ContentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PhotoResponse(
        UUID photoId,
        String url,
        Integer displayOrder,
        LocalDateTime createdAt,
        ContentStatus photoStatus
) {
}
