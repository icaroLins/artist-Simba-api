package com.icaro.simba_dj.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icaro.simba_dj.dto.request.photos.PhotoRequest;
import com.icaro.simba_dj.dto.request.platformLink.PlatformLinkRequest;
import com.icaro.simba_dj.dto.request.profiles.ProfileRequest;
import com.icaro.simba_dj.dto.request.songs.SongRequest;
import com.icaro.simba_dj.dto.request.videos.VideoRequest;
import com.icaro.simba_dj.model.email.EmailVerificationToken;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.repository.email.EmailVerificationRepository;
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
import java.time.LocalDateTime;
import java.util.List;
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

    @Autowired
    protected EmailVerificationRepository emailVerificationRepository;

    protected record TestUserContext(String token, UUID userId, UUID profileId) {}

    protected record  TestEventContext(String token, UUID userId, UUID profileId, UUID eventId) {}

    protected record TestPhotoContext(String token, UUID userId, UUID profileId, UUID photoId) {}

    protected record TestSongContext(String token, UUID userId, UUID profileId, UUID songId) {}

    protected record TestVideoContext(String token, UUID userId, UUID profileId, UUID videoId) {}

    protected String videoJson(String title, String url, String thumbnailUrl, Integer displayOrder) throws JsonProcessingException {
        VideoRequest videoRequest = new VideoRequest(title, url, thumbnailUrl, displayOrder);
        return new ObjectMapper().writeValueAsString(videoRequest);
    }

    protected TestVideoContext createUserAndVideo(String email, String password,
                                                  String nameArtist, String instagramUrl,
                                                  String title, String url, String thumbnailUrl,
                                                  Integer displayOrder) throws Exception {

        TestUserContext userContext = createUserAndProfile(email, password, nameArtist, instagramUrl);

        String json = videoJson(title, url, thumbnailUrl, displayOrder);

        MvcResult result = mockMvc.perform(post("/video/create")
                        .header("Authorization", "Bearer " + userContext.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        UUID videoId = UUID.fromString(JsonPath.read(responseBody, "$.videoId"));

        return new TestVideoContext(userContext.token(), userContext.userId(), userContext.profileId(), videoId);
    }

    protected EmailVerificationToken createTokenForUser(String email, String token, LocalDateTime expirationDate) {
        User user = userRepository.findByUserEmail(email);

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationToken.setExpirationDate(expirationDate);

        return emailVerificationRepository.save(verificationToken);
    }

    protected String songJson(String title, String coverUrl, Integer displayOrder,
                              List<PlatformLinkRequest> platformLinks) throws JsonProcessingException {
        SongRequest songRequest = new SongRequest(title, coverUrl, displayOrder, platformLinks);
        return new ObjectMapper().writeValueAsString(songRequest);
    }

    protected TestSongContext createUserAndSong(String email, String password,
                                                String nameArtist, String instagramUrl,
                                                String title, String coverUrl, Integer displayOrder,
                                                List<PlatformLinkRequest> platformLinks) throws Exception {

        TestUserContext userContext = createUserAndProfile(email, password, nameArtist, instagramUrl);

        String json = songJson(title, coverUrl, displayOrder, platformLinks);

        MvcResult result = mockMvc.perform(post("/song/create")
                        .header("Authorization", "Bearer " + userContext.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        UUID songId = UUID.fromString(JsonPath.read(responseBody, "$.songId"));

        return new TestSongContext(userContext.token(), userContext.userId(), userContext.profileId(), songId);
    }

    protected String photoJson(Integer displayOrder) throws JsonProcessingException {
        PhotoRequest photoRequest = new PhotoRequest(displayOrder);
        return new ObjectMapper().writeValueAsString(photoRequest);
    }

    protected TestPhotoContext createUserAndPhoto(String email, String password,
                                                  String nameArtist, String instagramUrl,
                                                  Integer displayOrder) throws Exception {

        TestUserContext userContext = createUserAndProfile(email, password, nameArtist, instagramUrl);

        String json = photoJson(displayOrder);

        MockMultipartFile photoPart = new MockMultipartFile(
                "photo", "", MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "photoImage",
                "test-photo.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes(StandardCharsets.UTF_8)
        );

        MvcResult result = mockMvc.perform(multipart("/photo/create")
                        .file(photoPart)
                        .file(imagePart)
                        .header("Authorization", "Bearer " + userContext.token()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        UUID photoId = UUID.fromString(JsonPath.read(responseBody, "$.photoId"));

        return new TestPhotoContext(userContext.token(), userContext.userId(), userContext.profileId(), photoId);
    }

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
