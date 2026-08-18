package com.icaro.simba_dj.dto.response.erro;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String code,
        String message,
        String path
) {
}
