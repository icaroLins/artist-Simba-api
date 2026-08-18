package com.icaro.simba_dj.controller;

import com.icaro.simba_dj.support.AbstractIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import com.icaro.simba_dj.controller.users.UserController;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.service.users.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sholdCreateUserSuccess() throws Exception {
        String json = userJson("icarolinsgomes30@gmail.com", "icaro123");

        mockMvc.perform(post("/simba/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value("icarolinsgomes30@gmail.com"))
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.userRole").value("ADMIN"))
                .andDo(print());

    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        String json = userJson("email-existente@test.com", "icaro123");

        mockMvc.perform(post("/simba/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(post("/simba/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("This email has already been registered."))
                .andExpect(jsonPath("$.path").value("/simba/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnBadRequestWhenUserEmailIsNull() throws Exception {
        String json = """
                {
                  "userEmail": null,
                  "userPassword_hash": "icaro123"
                }
                """;

        mockMvc.perform(post("/simba/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("userEmail: Email is required."))
                .andExpect(jsonPath("$.path").value("/simba/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnBadRequestWhenUserPasswordIsNull() throws Exception {
        String json = """
                {
                  "userEmail": "senha-null@test.com",
                  "userPassword_hash": null
                }
                """;

        mockMvc.perform(post("/simba/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("userPassword_hash: Password is required."))
                .andExpect(jsonPath("$.path").value("/simba/create"))
                .andDo(print());
    }

    @Test
    void shouldLoginUserSuccess() throws Exception {
        String email = "login-sucesso@test.com";
        String password = "icaro123";

      mockMvc.perform(post("/simba/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson(email, password)))
                .andExpect(status().isOk());

      User user = userRepository.findByUserEmail(email);
      user.setEmailVerified(true);
      userRepository.save(user);

        mockMvc.perform(post("/simba/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.userRole").value("ADMIN"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenLoginEmailIsIncorrect() throws Exception {
        String json = loginJson("email-inexistente@test.com", "icaro123");

        mockMvc.perform(post("/simba/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password."))
                .andExpect(jsonPath("$.path").value("/simba/login"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenLoginPasswordIsIncorrect() throws Exception {
        String email = "login-senha-incorreta@test.com";

        mockMvc.perform(post("/simba/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson(email, "senha-correta")))
                .andExpect(status().isOk());

        mockMvc.perform(post("/simba/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(email, "senha-incorreta")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password."))
                .andExpect(jsonPath("$.path").value("/simba/login"))
                .andDo(print());
    }

    @Test
    void shouldReturnBadRequestWhenLoginEmailIsNull() throws Exception {
        String json = """
                {
                  "email": null,
                  "password": "icaro123"
                }
                """;

        mockMvc.perform(post("/simba/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("email: Email is required."))
                .andExpect(jsonPath("$.path").value("/simba/login"))
                .andDo(print());
    }

    @Test
    void shouldReturnBadRequestWhenLoginPasswordIsNull() throws Exception {
        String json = """
                {
                  "email": "login-senha-null@test.com",
                  "password": null
                }
                """;

        mockMvc.perform(post("/simba/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("password: Password is required."))
                .andExpect(jsonPath("$.path").value("/simba/login"))
                .andDo(print());
    }

    @Test
    void shouldFindLoggedUserSuccess() throws Exception {
        String email = "usuario-logado@test.com";
        String password = "icaro123";
        String token = createUserAndLogin(email, password);

        mockMvc.perform(get("/simba/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value(email))
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.userRole").value("ADMIN"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenFindUserWithoutToken() throws Exception {
        mockMvc.perform(get("/simba/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/simba/me"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenFindUserWithInvalidToken() throws Exception {
        mockMvc.perform(get("/simba/me")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/simba/me"))
                .andDo(print());
    }

    protected String createUserAndLogin(String email, String password) throws Exception {
        mockMvc.perform(post("/simba/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson(email, password)))
                .andExpect(status().isOk());

        User user = userRepository.findByUserEmail(email);
        user.setEmailVerified(true);
        userRepository.save(user);

        MvcResult result = mockMvc.perform(post("/simba/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }
}
