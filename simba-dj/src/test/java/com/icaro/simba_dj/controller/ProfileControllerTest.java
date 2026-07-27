package com.icaro.simba_dj.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icaro.simba_dj.dto.request.profiles.ProfileRequest;
import com.icaro.simba_dj.support.AbstractIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ProfileControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void shouldCreateProfileSuccessfully() throws Exception {
        String email = "create-profile@test.com";
        String password = "icaro123";
        String profileNameArtist = "testeNamaArtist";
        String profileInstagramUrl = "teste/url/instagram";
        String token = createUserAndLogin(email, password);
        String json = profileJson(profileNameArtist,profileInstagramUrl);


        MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "profileImage",
                "test-imajge.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes(StandardCharsets.UTF_8)
        );


        mockMvc.perform(multipart("/profile/create")
                        .file(profilePart)
                        .file(imagePart)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    void shouldReturnSuccessfullyWhenCreateProfileImagenNull() throws Exception {
        String email = "profile-succesimage@test.com";
        String password = "icaro123";
        String profileNameArtist = "Teste-succesimageNull";
        String profileInstagramUrl = "teste/url/instagram";

        String token = createUserAndLogin(email, password);
        String json = profileJson(profileNameArtist,profileInstagramUrl);

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/profile/create")
                        .file(profilePart)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldReturnBadRequestWhenCreateProfilePartNameArtistNull() throws Exception {
        String email = "profile-badcreate@test.com";
        String password = "icaro123";

        String profileInstagramUrl = "teste/url/instagram";

        String token = createUserAndLogin(email, password);
        String json = profileJson(null,profileInstagramUrl);

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "profileImage",
                "test-imajge.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes(StandardCharsets.UTF_8)
        );


        mockMvc.perform(multipart("/profile/create")
                        .file(profilePart)
                        .file(imagePart)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("profileNameArtist: Artist name is required."))
                .andExpect(jsonPath("$.path").value("/profile/create"))
                .andDo(print());

    }

    @Test
    void shouldReturnBadRequestWhenCreateProfilePartInstagramUrlNull()  throws Exception {
        String email = "profile-badcreate-instagramurl@test.com";
        String password = "icaro123";
        String profileNameArtist = "teste-InstagramUrl-badRequest";

        String token = createUserAndLogin(email, password);
        String json = profileJson(profileNameArtist,null);

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "profileImage",
                "test-imajge.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes(StandardCharsets.UTF_8)
        );


        mockMvc.perform(multipart("/profile/create")
                        .file(profilePart)
                        .file(imagePart)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("profileInstagramUrl: Instagram URL is required."))
                .andExpect(jsonPath("$.path").value("/profile/create"))
                .andDo(print());

    }

    @Test
    void shouldReturnUnauthorizedWhenNoTokenProvided() throws Exception {
        String json = profileJson("testeNamaArtist", "teste/url/instagram");

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/profile/create")
                        .file(profilePart))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/profile/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenInvalid() throws Exception {
        String json = profileJson("testeNamaArtist", "teste/url/instagram");

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile", "", MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/profile/create")
                        .file(profilePart)
                        .header("Authorization", "Bearer token.invalid.test"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/profile/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessWhenUpdateProfile() throws Exception {
        String email = "test-update@test.com";
        String password = "icaro123";
        String nameArtist = "test-namaArtistUpdate";
        String instagramUrl = "test-instagramUrlUpdate";

        TestUserContext context = createUserAndProfile(email, password, nameArtist, instagramUrl);

        String updatedJson = profileJson("novoNomeArtista", "novo/instagram/url");

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile", "", MediaType.APPLICATION_JSON_VALUE,
                updatedJson.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "profileImage",
                "test-imajge.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/profile/update/" + context.profileId())
                        .file(profilePart)
                        .file(imagePart)
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessWhenUpdateProfileImageNull() throws Exception {
        String email = "test-update-image-null@test.com";
        String password = "icaro123";
        String nameArtist = "test-namaArtistUpdate";
        String instagramUrl = "test-instagramUrlUpdate";

        TestUserContext context = createUserAndProfile(email, password, nameArtist, instagramUrl);

        String updatedJson = profileJson("novoNomeArtista", "novo/instagram/url");

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile", "", MediaType.APPLICATION_JSON_VALUE,
                updatedJson.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/profile/update/" + context.profileId())
                        .file(profilePart)
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessWhenUpdateProfileNameArtistNull() throws Exception {
        String email = "test-update-artist-null@test.com";
        String password = "icaro123";
        String nameArtist = "test-namaArtistUpdate";
        String instagramUrl = "test-instagramUrlUpdate";

        TestUserContext context = createUserAndProfile(email, password, nameArtist, instagramUrl);

        String updatedJson = profileJson(null, "novo/nameNull/instagram/url");

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile", "", MediaType.APPLICATION_JSON_VALUE,
                updatedJson.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/profile/update/" + context.profileId())
                        .file(profilePart)
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessWhenUpdateProfileInstagramUrlNull() throws Exception {
        String email = "test-update-instagram-null@test.com";
        String password = "icaro123";
        String nameArtist = "test-namaArtistUpdate";
        String instagramUrl = "test-instagramUrlUpdate";

        TestUserContext context = createUserAndProfile(email, password, nameArtist, instagramUrl);

        String updatedJson = profileJson("nullInstagramNameArtist", null);

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile", "", MediaType.APPLICATION_JSON_VALUE,
                updatedJson.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/profile/update/" + context.profileId())
                        .file(profilePart)
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessWhenUpdateProfileInstagramUrlNullAndNameArtistNull() throws Exception {
        String email = "test-update-image@test.com";
        String password = "icaro123";
        String nameArtist = "test-namaArtistUpdate";
        String instagramUrl = "test-instagramUrlUpdate";

        TestUserContext context = createUserAndProfile(email, password, nameArtist, instagramUrl);

        MockMultipartFile imagePart = new MockMultipartFile(
                "profileImage",
                "test-imajge.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/profile/update/" + context.profileId())
                        .file(imagePart)
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenUpdateProfileNotToken()throws Exception {
        String email = "test-update-unauthorized-not-token@test.com";
        String password = "icaro123";
        String nameArtist = "test-namaArtistUpdate";
        String instagramUrl = "test-instagramUrlUpdate";

        TestUserContext context = createUserAndProfile(email, password, nameArtist, instagramUrl);

        String updatedJson = profileJson("novoNomeArtista", "novo/instagram/url");

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile", "", MediaType.APPLICATION_JSON_VALUE,
                updatedJson.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "profileImage",
                "test-imajge.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/profile/update/" + context.profileId())
                        .file(profilePart)
                        .file(imagePart))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/profile/update/"+ context.profileId()))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenUpdateProfileTokenInvalid() throws Exception {
        String email = "test-update-unauthorized-invalid-token@test.com";
        String password = "icaro123";
        String nameArtist = "test-namaArtistUpdate";
        String instagramUrl = "test-instagramUrlUpdate";

        TestUserContext context = createUserAndProfile(email, password, nameArtist, instagramUrl);

        String updatedJson = profileJson("novoNomeArtista", "novo/instagram/url");

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile", "", MediaType.APPLICATION_JSON_VALUE,
                updatedJson.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "profileImage",
                "test-imajge.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/profile/update/" + context.profileId())
                        .file(profilePart)
                        .file(imagePart)
                        .header("Authorization", "Bearer " + "invalidTokenMeTest"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/profile/update/"+ context.profileId()))
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessFullyFindProfile() throws Exception {
        String email = "test-find-profile@test.com";
        String password = "icaro123";
        String nameArtist = "test-namaArtist";
        String instagramUrl = "test-instagramUrl";

        TestUserContext context = createUserAndProfile(email, password, nameArtist, instagramUrl);

        mockMvc.perform(get("/profile/me")
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileId").value(context.profileId().toString()))
                .andExpect(jsonPath("$.profileNameArtist").value(nameArtist))
                .andExpect(jsonPath("$.profileInstagramUrl").value(instagramUrl))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenFindProfileNotToken()throws Exception {
        String email = "test-find-profile-not-token@test.com";
        String password = "icaro123";
        String nameArtist = "test-namaArtist";
        String instagramUrl = "test-instagramUrl";

        createUserAndProfile(email, password, nameArtist, instagramUrl);

        mockMvc.perform(get("/profile/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/profile/me"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenFindProfileTokenInvalid()throws Exception {
        String email = "test-find-profile-invalid-token@test.com";
        String password = "icaro123";
        String nameArtist = "test-namaArtist";
        String instagramUrl = "test-instagramUrl";

        createUserAndProfile(email, password, nameArtist, instagramUrl);

        mockMvc.perform(get("/profile/me")
                        .header("Authorization", "Bearer " + "invalidTokenMeTest"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/profile/me"))
                .andDo(print());
    }

}
