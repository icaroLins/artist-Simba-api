package com.icaro.simba_dj.controller;

import com.icaro.simba_dj.model.passwordReset.PasswordResetToken;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.repository.passwordReset.PasswordResetRepository;
import com.icaro.simba_dj.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PasswordResetControllerTest extends AbstractIntegrationTest {

    @MockitoBean
    private JavaMailSender javaMailSender;

    @org.springframework.beans.factory.annotation.Autowired
    private PasswordResetRepository passwordResetRepository;

    private String requestJson(String email) {
        return """
                {
                  "email": "%s"
                }
                """.formatted(email);
    }

    private String confirmJson(String token, String newPassword) {
        return """
                {
                  "token": "%s",
                  "newPassword": "%s"
                }
                """.formatted(token, newPassword);
    }

    @Test
    void shouldReturnGenericMessageWhenRequestResetForExistingUser() throws Exception {
        String email = "reset-existing@test.com";
        createUser(email, "icaro123");

        User user = userRepository.findByUserEmail(email);
        user.setEmailVerified(true);
        userRepository.save(user);

        mockMvc.perform(post("/password/reset/request")
                        .contentType("application/json")
                        .content(requestJson(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Se o e-mail estiver cadastrado, você receberá um link para redefinição de senha."))
                .andDo(print());

        Optional<PasswordResetToken> token = passwordResetRepository.findByUser(user);
        assertTrue(token.isPresent());

        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(mailCaptor.capture());
        assertEquals(email, mailCaptor.getValue().getTo()[0]);
        assertNotNull(mailCaptor.getValue().getText());
    }

    @Test
    void shouldReturnGenericMessageWhenRequestResetForMissingUser() throws Exception {
        mockMvc.perform(post("/password/reset/request")
                        .contentType("application/json")
                        .content(requestJson("missing-user@test.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Se o e-mail estiver cadastrado, você receberá um link para redefinição de senha."))
                .andDo(print());

        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldResetPasswordWithValidToken() throws Exception {
        String email = "reset-valid@test.com";
        String oldPassword = "icaro123";
        String newPassword = "novaSenha123";

        createUser(email, oldPassword);
        User user = userRepository.findByUserEmail(email);
        user.setEmailVerified(true);
        userRepository.save(user);

        mockMvc.perform(post("/password/reset/request")
                        .contentType("application/json")
                        .content(requestJson(email)))
                .andExpect(status().isOk())
                .andDo(print());

        PasswordResetToken token = passwordResetRepository.findByUser(user).orElseThrow();
        String tokenValue = token.getToken();

        mockMvc.perform(post("/password/reset/confirm")
                        .contentType("application/json")
                        .content(confirmJson(tokenValue, newPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Senha redefinida com sucesso."))
                .andDo(print());

        User updatedUser = userRepository.findByUserEmail(email);
        assertFalse(passwordResetRepository.findByToken(tokenValue).isPresent());
        assertNotEquals(oldPassword, updatedUser.getUserPassword_hash());

        mockMvc.perform(post("/simba/login")
                        .contentType("application/json")
                        .content(loginJson(email, newPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenResetTokenExpired() throws Exception {
        String email = "reset-expired@test.com";
        createUser(email, "icaro123");
        User user = userRepository.findByUserEmail(email);
        user.setEmailVerified(true);
        userRepository.save(user);

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpirationDate(LocalDateTime.now().minusMinutes(1));
        passwordResetRepository.save(token);

        mockMvc.perform(post("/password/reset/confirm")
                        .contentType("application/json")
                        .content(confirmJson(token.getToken(), "novaSenha123")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("token expired"))
                .andExpect(jsonPath("$.path").value("/password/reset/confirm"))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenResetTokenDoesNotExist() throws Exception {
        mockMvc.perform(post("/password/reset/confirm")
                        .contentType("application/json")
                        .content(confirmJson("missing-token", "novaSenha123")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Token not found"))
                .andExpect(jsonPath("$.path").value("/password/reset/confirm"))
                .andDo(print());
    }

    @Test
    void shouldReplaceExistingResetTokenWhenRequestedTwice() throws Exception {
        String email = "reset-twice@test.com";
        createUser(email, "icaro123");
        User user = userRepository.findByUserEmail(email);
        user.setEmailVerified(true);
        userRepository.save(user);

        mockMvc.perform(post("/password/reset/request")
                        .contentType("application/json")
                        .content(requestJson(email)))
                .andExpect(status().isOk());

        PasswordResetToken firstToken = passwordResetRepository.findByUser(user).orElseThrow();
        String firstTokenValue = firstToken.getToken();

        mockMvc.perform(post("/password/reset/request")
                        .contentType("application/json")
                        .content(requestJson(email)))
                .andExpect(status().isOk());

        PasswordResetToken secondToken = passwordResetRepository.findByUser(user).orElseThrow();
        String secondTokenValue = secondToken.getToken();

        assertNotEquals(firstTokenValue, secondTokenValue);
        assertEquals(firstToken.getId(), secondToken.getId());
    }
}
