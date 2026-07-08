package com.icaro.simba_dj.dto.request.users;

public record UserRequest(
        String userEmail,
        String userPasseord_hash
) {}
