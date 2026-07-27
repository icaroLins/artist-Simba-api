package com.icaro.simba_dj.dto.response.platformLink;

import com.icaro.simba_dj.enums.MusicPlatform;

import java.util.UUID;

public record PlatformLinkResponse(
        UUID platformLinkId,
        MusicPlatform platform,
        String externalLink
) {
}
