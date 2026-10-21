package com.icaro.simba_dj.controller;

import com.icaro.simba_dj.model.email.EmailVerificationToken;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

public class EmailVerificationControllerTest extends AbstractIntegrationTest {

    @MockitoBean
    private JavaMailSender javaMailSender;

    // ---------- VERIFY EMAIL ----------

    @Test
    void shouldReturnSuccessWhenVerifyEmailTokenValid() throws Exception {
        String email = "verify-email-success@test.com";
        createUserAndLogin(email, "icaro123");

        String token = "valid-token-123";
        createTokenForUser(email, token, LocalDateTime.now().plusMinutes(10));

        mockMvc.perform(get("/mail/api/verify-email")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("E-mail verificado com sucesso"))
                .andDo(print());

        User user = userRepository.findByUserEmail(email);
        assertTrue(user.getEmailVerified());

        Optional<EmailVerificationToken> deletedToken = emailVerificationRepository.findByToken(token);
        assertTrue(deletedToken.isEmpty());
    }

    @Test
    void shouldReturnNotFoundWhenVerifyEmailTokenNotFound() throws Exception {
        mockMvc.perform(get("/mail/api/verify-email")
                        .param("token", "nonexistent-token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Token not found"))
                .andExpect(jsonPath("$.path").value("/mail/api/verify-email"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenVerifyEmailTokenExpired() throws Exception {
        String email = "verify-email-expired@test.com";
        createUser(email, "icaro123");

        String token = "expired-token-123";
        createTokenForUser(email, token, LocalDateTime.now().minusMinutes(1));

        mockMvc.perform(get("/mail/api/verify-email")
                        .param("token", token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Token is expired"))
                .andExpect(jsonPath("$.path").value("/mail/api/verify-email"))
                .andDo(print());

        User user = userRepository.findByUserEmail(email);
        assertFalse(user.getEmailVerified());
    }

    @Test
    void shouldReturnBadRequestWhenVerifyEmailTokenParamMissing() throws Exception {
        mockMvc.perform(get("/mail/api/verify-email"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    // ---------- RESEND VERIFICATION ----------

    @Test
    void shouldReturnSuccessWhenResendVerification() throws Exception {
        String email = "resend-verification-success@test.com";
        createUser(email, "icaro123");

        mockMvc.perform(post("/mail/api/resend-verification")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("E-mail de verificação reenviado"))
                .andDo(print());

        User user = userRepository.findByUserEmail(email);
        Optional<EmailVerificationToken> token = emailVerificationRepository.findByUser(user);
        assertTrue(token.isPresent());
    }

    @Test
    void shouldReturnBadRequestWhenResendVerificationUserNotFound() throws Exception {
        mockMvc.perform(post("/mail/api/resend-verification")
                        .param("email", "nonexistent@test.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found"))
                .andDo(print());
    }

    @Test
    void shouldReturnBadRequestWhenResendVerificationEmailAlreadyVerified() throws Exception {
        String email = "resend-verification-already-verified@test.com";
        createUser(email, "icaro123");

        User user = userRepository.findByUserEmail(email);
        user.setEmailVerified(true);
        userRepository.save(user);

        mockMvc.perform(post("/mail/api/resend-verification")
                        .param("email", email))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("E-mail já verificado"))
                .andDo(print());
    }

    @Test
    void shouldReuseExistingTokenWhenResendVerificationCalledTwice() throws Exception {
        String email = "resend-verification-twice@test.com";
        createUser(email, "icaro123");

        mockMvc.perform(post("/mail/api/resend-verification")
                        .param("email", email))
                .andExpect(status().isOk());

        User user = userRepository.findByUserEmail(email);
        String firstToken = emailVerificationRepository.findByUser(user).orElseThrow().getToken();

        mockMvc.perform(post("/mail/api/resend-verification")
                        .param("email", email))
                .andExpect(status().isOk());

        String secondToken = emailVerificationRepository.findByUser(user).orElseThrow().getToken();

        assertNotEquals(firstToken, secondToken);
        assertEquals(1, emailVerificationRepository.findByUser(user).stream().count());
    }
}