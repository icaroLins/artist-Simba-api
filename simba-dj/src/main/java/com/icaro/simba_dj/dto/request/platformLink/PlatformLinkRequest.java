package com.icaro.simba_dj.dto.request.platformLink;

import com.icaro.simba_dj.enums.MusicPlatform;

public record PlatformLinkRequest(
        MusicPlatform platform,
        String externalLink
) {
}
