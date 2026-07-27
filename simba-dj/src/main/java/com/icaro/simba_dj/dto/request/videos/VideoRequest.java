package com.icaro.simba_dj.dto.request.videos;

public record VideoRequest(
    String title,
    String url,
    String thumbnailUrl,
    Integer displayOrder

) {
}
