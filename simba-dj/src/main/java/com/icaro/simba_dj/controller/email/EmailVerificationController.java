package com.icaro.simba_dj.controller.email;

import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.repository.users.UserRepository;
import com.icaro.simba_dj.service.email.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mail/api")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final UserRepository userRepository;

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        emailVerificationService.verifyToken(token);
        return ResponseEntity.ok(Map.of("message", "E-mail verificado com sucesso"));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestParam String email) {
        User user = userRepository.findByUserEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (user.getEmailVerified()){
            return ResponseEntity.badRequest().body(Map.of("message", "E-mail já verificado"));
        }

        emailVerificationService.createAndSendVerificationToken(user);
        return ResponseEntity.ok(Map.of("message", "E-mail de verificação reenviado"));
    }
}
