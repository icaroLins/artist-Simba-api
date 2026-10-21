package com.icaro.simba_dj.service.email;

import com.icaro.simba_dj.exception.ResourceNotFoundException;
import com.icaro.simba_dj.exception.UnauthorizedException;
import com.icaro.simba_dj.model.email.EmailVerificationToken;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.repository.email.EmailVerificationRepository;
import com.icaro.simba_dj.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    @Autowired
    private final EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private final UserRepository userRepository;

    private final JavaMailSender javaMailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private void sendVerificationEmail(String emailTo, String token) {
        String verificationLink = frontendUrl + "/verify-email/confirm?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("simba.manege@gmail.com");
        message.setTo(emailTo);
        message.setSubject("Simba - Email Verification");
        message.setText("Simba - Click the link to confirm your registration: " + verificationLink);

        javaMailSender.send(message);
    }

    @Transactional
    public void createAndSendVerificationToken(User user){
        EmailVerificationToken emailVerificationToken =
                emailVerificationRepository.findByUser(user)
                        .orElse(new EmailVerificationToken());

        emailVerificationToken.setUser(user);
        emailVerificationToken.setToken(UUID.randomUUID().toString());
        emailVerificationToken.setExpirationDate(LocalDateTime.now().plusMinutes(10));

        emailVerificationRepository.save(emailVerificationToken);

        sendVerificationEmail(user.getUserEmail(), emailVerificationToken.getToken());
    }

    public void verifyToken(String token){
        EmailVerificationToken verificationToken = emailVerificationRepository.findByToken(token)
                .orElseThrow(()-> new ResourceNotFoundException("Token not found"));

        if(verificationToken.isExpired()){
            throw new UnauthorizedException(
                    "TOKEN_EXPIRED",
                    "Token is expired"
            );
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        emailVerificationRepository.delete(verificationToken);
    }
}
