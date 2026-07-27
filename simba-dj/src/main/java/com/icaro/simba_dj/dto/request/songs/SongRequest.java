package com.icaro.simba_dj.dto.request.songs;

import com.icaro.simba_dj.dto.request.platformLink.PlatformLinkRequest;

import java.util.List;

public record SongRequest(
        String title,
        String coverUrl,
        Integer displayOrder,
        List<PlatformLinkRequest> platformLinks
) {
}
