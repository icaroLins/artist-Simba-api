package com.icaro.simba_dj.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icaro.simba_dj.dto.request.profiles.ProfileRequest;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.repository.users.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AbstractIntegrationTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserRepository userRepository;

    protected record TestUserContext(String token, UUID userId, UUID profileId) {}

    protected record  TestEventContext(String token, UUID userId, UUID profileId, UUID eventId) {}

    protected String eventJson(String eventTitle, LocalDate eventDate, String eventTicketLink, String eventLocation){
        return """
                {
                    "eventTitle": %s,
                    "eventDate": %s,
                    "eventTicketLink": %s,
                    "eventLocation": %s
                }
                """.formatted(
                toJsonValue(eventTitle),
                toJsonValue(eventDate),
                toJsonValue(eventTicketLink),
                toJsonValue(eventLocation)
        );
    }


    private String toJsonValue(Object value) {
        return value == null ? "null" : "\"" + value + "\"";
    }


    protected String profileJson(String profileNameArtist, String profileInstagramUrl) throws JsonProcessingException {
        ProfileRequest profileRequest = new ProfileRequest(profileNameArtist, profileInstagramUrl);
        return new ObjectMapper().writeValueAsString(profileRequest);
    }

    protected String userJson(String email, String password) {
        return """
                {
                  "userEmail": "%s",
                  "userPassword_hash": "%s"
                }
                """.formatted(email, password);
    }

    protected String loginJson(String email, String password) {
        return """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);
    }


    protected String createUserAndLogin(String email, String password) throws Exception {
        mockMvc.perform(post("/simba/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson(email, password)))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(post("/simba/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }
    protected TestUserContext createUserAndProfile(String email, String password,
                                                   String nameArtist, String instagramUrl) throws Exception {

        String token = createUserAndLogin(email, password);
        String json = profileJson(nameArtist, instagramUrl);

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile", "", MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8)
        );

        MvcResult result = mockMvc.perform(multipart("/profile/create")
                        .file(profilePart)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        UUID profileId = UUID.fromString(JsonPath.read(responseBody, "$.profileId"));
        UUID userId = UUID.fromString(JsonPath.read(responseBody, "$.userId"));

        return new TestUserContext(token, userId, profileId);
    }

    protected TestEventContext createEvent(String eventTitle, LocalDate eventDate,
                                           String eventTicketLink,
                                           String eventLocation, TestUserContext context) throws Exception {

        String json = eventJson(eventTitle, eventDate, eventTicketLink, eventLocation);

        User user = userRepository.findById(context.userId)
                        .orElseThrow(() -> new Exception("User not found"));
        user.setEmailVerified(true);
        userRepository.save(user);

        MvcResult result = mockMvc.perform(post("/event/create")
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        UUID eventId = UUID.fromString(JsonPath.read(responseBody, "$.eventId"));

        return new TestEventContext(context.token(), context.userId(), context.profileId(), eventId);
    }

}
