package com.icaro.simba_dj.controller.passwordReset;

import com.icaro.simba_dj.dto.request.passwordReset.PasswordResetConfirmRequest;
import com.icaro.simba_dj.dto.request.passwordReset.PasswordResetRequest;
import com.icaro.simba_dj.dto.response.passwordReset.PasswordResetResponse;
import com.icaro.simba_dj.service.passwordReset.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/password/reset")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/request")
    public ResponseEntity<PasswordResetResponse> requestPasswordReset(
            @RequestBody @Valid PasswordResetRequest request
    ) {
        return ResponseEntity.ok(passwordResetService.requestPasswordReset(request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<PasswordResetResponse> confirmPasswordReset(
            @RequestBody @Valid PasswordResetConfirmRequest request
    ) {
        return ResponseEntity.ok(passwordResetService.resetPassword(request));
    }
}
