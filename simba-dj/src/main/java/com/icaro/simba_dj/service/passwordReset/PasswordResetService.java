package com.icaro.simba_dj.service.passwordReset;

import com.icaro.simba_dj.dto.request.passwordReset.PasswordResetConfirmRequest;
import com.icaro.simba_dj.dto.request.passwordReset.PasswordResetRequest;
import com.icaro.simba_dj.dto.response.passwordReset.PasswordResetResponse;
import com.icaro.simba_dj.exception.ResourceNotFoundException;
import com.icaro.simba_dj.exception.UnauthorizedException;
import com.icaro.simba_dj.model.passwordReset.PasswordResetToken;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.repository.passwordReset.PasswordResetRepository;
import com.icaro.simba_dj.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private static final String REQUEST_MESSAGE =
            "Se o e-mail estiver cadastrado, voc\u00ea receber\u00e1 um link para redefini\u00e7\u00e3o de senha.";
    private static final String SUCCESS_MESSAGE = "Senha redefinida com sucesso.";

    @Autowired
    private final PasswordResetRepository passwordResetRepository;

    @Autowired
    private final UserRepository userRepository;

    private final JavaMailSender javaMailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private void sendResetPassword(String emailTo, String token) {
        String resetLink = frontendUrl + "/reset?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("simba.manege@gmail.com");
        message.setTo(emailTo);
        message.setSubject("Simba - Reset Password");
        message.setText("Simba - Click the link to reset your password: " + resetLink);
        javaMailSender.send(message);
    }

    @Transactional
    public PasswordResetResponse requestPasswordReset(PasswordResetRequest request) {
        String email = request.email();
        User user = userRepository.findByUserEmail(email);

        if (user == null) {
            return new PasswordResetResponse(REQUEST_MESSAGE);
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = passwordResetRepository.findByUser(user)
                .orElseGet(PasswordResetToken::new);
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(token);
        passwordResetToken.setExpirationDate(LocalDateTime.now().plusMinutes(5));

        passwordResetRepository.save(passwordResetToken);

        sendResetPassword(user.getUserEmail(), passwordResetToken.getToken());

        return new PasswordResetResponse(REQUEST_MESSAGE);
    }


    @Transactional
    public PasswordResetResponse resetPassword(PasswordResetConfirmRequest request) {
        String token = request.token();
        String newPassword = request.newPassword();

        PasswordResetToken passwordResetToken = passwordResetRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        if(passwordResetToken.isExpired()){
            throw new UnauthorizedException(
                    "TOKEN_RESET_EXPIRED",
                    "token expired"
            );
        }

        User user = passwordResetToken.getUser();
        user.setUserPassword_hash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetRepository.delete(passwordResetToken);

        return new PasswordResetResponse(SUCCESS_MESSAGE);
    }
}
