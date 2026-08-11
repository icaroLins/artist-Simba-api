package com.icaro.simba_dj.controller;

import com.icaro.simba_dj.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VideoControllerTest extends AbstractIntegrationTest {

    // ---------- CREATE ----------

    @Test
    void shouldReturnSuccessfullyCreatedVideo() throws Exception {
        TestUserContext context = createUserAndProfile("create-video@test.com",
                "icaro123", "IkratoVideo", "createvideo/url/instagram");

        String json = videoJson("Live Set 2026", "https://youtube.com/watch?v=abc123",
                "https://img.youtube.com/vi/abc123/hqdefault.jpg", 1);

        mockMvc.perform(post("/video/create")
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoId").exists())
                .andExpect(jsonPath("$.title").value("Live Set 2026"))
                .andExpect(jsonPath("$.url").value("https://youtube.com/watch?v=abc123"))
                .andExpect(jsonPath("$.thumbnailUrl").value("https://img.youtube.com/vi/abc123/hqdefault.jpg"))
                .andExpect(jsonPath("$.displayOrder").value(1))
                .andExpect(jsonPath("$.videoStatus").value("ACTIVE"))
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessfullyCreatedVideoWhenThumbnailNull() throws Exception {
        TestUserContext context = createUserAndProfile("create-video-no-thumb@test.com",
                "icaro123", "IkratoVideoNoThumb", "createvideonothumb/url/instagram");

        String json = videoJson("Set Without Thumb", "https://youtube.com/watch?v=xyz789",
                null, 1);

        mockMvc.perform(post("/video/create")
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thumbnailUrl").doesNotExist())
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenCreateVideoNotToken() throws Exception {
        String json = videoJson("Live Set", "https://youtube.com/watch?v=abc123", null, 1);

        mockMvc.perform(post("/video/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/video/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenCreateVideoTokenInvalid() throws Exception {
        String json = videoJson("Live Set", "https://youtube.com/watch?v=abc123", null, 1);

        mockMvc.perform(post("/video/create")
                        .header("Authorization", "Bearer token.invalid.test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/video/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenCreateVideoProfileNotFound() throws Exception {
        String token = createUserAndLogin("create-video-no-profile@test.com", "icaro123");
        String json = videoJson("Live Set", "https://youtube.com/watch?v=abc123", null, 1);

        mockMvc.perform(post("/video/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profile not found"))
                .andExpect(jsonPath("$.path").value("/video/create"))
                .andDo(print());
    }

    // ---------- UPDATE ----------

    @Test
    void shouldReturnSuccessWhenUpdateVideo() throws Exception {
        TestVideoContext context = createUserAndVideo("update-video@test.com",
                "icaro123", "IkratoUpdateVideo", "updatevideo/url/instagram",
                "Original Title", "https://youtube.com/watch?v=original", "https://img.url/original.jpg", 1);

        String updatedJson = videoJson("New Title", "https://youtube.com/watch?v=new",
                "https://img.url/new.jpg", 2);

        mockMvc.perform(patch("/video/update/" + context.videoId())
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoId").value(context.videoId().toString()))
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.url").value("https://youtube.com/watch?v=new"))
                .andExpect(jsonPath("$.thumbnailUrl").value("https://img.url/new.jpg"))
                .andExpect(jsonPath("$.displayOrder").value(2))
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessWhenUpdateVideoFieldsNullKeepsOriginal() throws Exception {
        TestVideoContext context = createUserAndVideo("update-video-null@test.com",
                "icaro123", "IkratoUpdateVideoNull", "updatevideonull/url/instagram",
                "Original Title", "https://youtube.com/watch?v=original", "https://img.url/original.jpg", 1);

        String updatedJson = videoJson(null, null, null, 5);

        mockMvc.perform(patch("/video/update/" + context.videoId())
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Original Title"))
                .andExpect(jsonPath("$.url").value("https://youtube.com/watch?v=original"))
                .andExpect(jsonPath("$.thumbnailUrl").value("https://img.url/original.jpg"))
                .andExpect(jsonPath("$.displayOrder").value(5))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenUpdateVideoNotToken() throws Exception {
        TestVideoContext context = createUserAndVideo("update-video-unauthorized@test.com",
                "icaro123", "IkratoUpdateUnauth", "updateunauth/url/instagram",
                "Title", "https://youtube.com/watch?v=x", null, 1);

        String updatedJson = videoJson("New Title", "https://youtube.com/watch?v=new", null, 2);

        mockMvc.perform(patch("/video/update/" + context.videoId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/video/update/" + context.videoId()))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenUpdateVideoTokenInvalid() throws Exception {
        TestVideoContext context = createUserAndVideo("update-video-invalid-token@test.com",
                "icaro123", "IkratoUpdateInvalid", "updateinvalid/url/instagram",
                "Title", "https://youtube.com/watch?v=x", null, 1);

        String updatedJson = videoJson("New Title", "https://youtube.com/watch?v=new", null, 2);

        mockMvc.perform(patch("/video/update/" + context.videoId())
                        .header("Authorization", "Bearer invalidTokenMeTest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/video/update/" + context.videoId()))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenUpdateVideoVideoNotFound() throws Exception {
        TestUserContext context = createUserAndProfile("update-video-not-found@test.com",
                "icaro123", "IkratoUpdateVideoNotFound", "updatevideonotfound/url/instagram");

        UUID randomVideoId = UUID.randomUUID();
        String updatedJson = videoJson("New Title", "https://youtube.com/watch?v=new", null, 2);

        mockMvc.perform(patch("/video/update/" + randomVideoId)
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Video not found"))
                .andExpect(jsonPath("$.path").value("/video/update/" + randomVideoId))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenUpdateVideoProfileNotFound() throws Exception {
        String token = createUserAndLogin("update-video-no-profile@test.com", "icaro123");
        UUID randomVideoId = UUID.randomUUID();
        String updatedJson = videoJson("New Title", "https://youtube.com/watch?v=new", null, 2);

        mockMvc.perform(patch("/video/update/" + randomVideoId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profile not found"))
                .andExpect(jsonPath("$.path").value("/video/update/" + randomVideoId))
                .andDo(print());
    }

    // ---------- DELETE ----------

    @Test
    void shouldReturnSuccessWhenDeleteVideo() throws Exception {
        TestVideoContext context = createUserAndVideo("delete-video@test.com",
                "icaro123", "IkratoDeleteVideo", "deletevideo/url/instagram",
                "Title", "https://youtube.com/watch?v=x", null, 1);

        mockMvc.perform(delete("/video/delete/" + context.videoId())
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/video/list")
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenDeleteVideoNotToken() throws Exception {
        TestVideoContext context = createUserAndVideo("delete-video-unauthorized@test.com",
                "icaro123", "IkratoDeleteUnauth", "deleteunauth/url/instagram",
                "Title", "https://youtube.com/watch?v=x", null, 1);

        mockMvc.perform(delete("/video/delete/" + context.videoId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/video/delete/" + context.videoId()))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenDeleteVideoTokenInvalid() throws Exception {
        TestVideoContext context = createUserAndVideo("delete-video-invalid-token@test.com",
                "icaro123", "IkratoDeleteInvalid", "deleteinvalid/url/instagram",
                "Title", "https://youtube.com/watch?v=x", null, 1);

        mockMvc.perform(delete("/video/delete/" + context.videoId())
                        .header("Authorization", "Bearer invalidTokenMeTest"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/video/delete/" + context.videoId()))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteVideoVideoNotFound() throws Exception {
        TestUserContext context = createUserAndProfile("delete-video-not-found@test.com",
                "icaro123", "IkratoDeleteVideoNotFound", "deletevideonotfound/url/instagram");

        UUID randomVideoId = UUID.randomUUID();

        mockMvc.perform(delete("/video/delete/" + randomVideoId)
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Video not found"))
                .andExpect(jsonPath("$.path").value("/video/delete/" + randomVideoId))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteVideoProfileNotFound() throws Exception {
        String token = createUserAndLogin("delete-video-no-profile@test.com", "icaro123");
        UUID randomVideoId = UUID.randomUUID();

        mockMvc.perform(delete("/video/delete/" + randomVideoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profile not found"))
                .andExpect(jsonPath("$.path").value("/video/delete/" + randomVideoId))
                .andDo(print());
    }

    // ---------- LIST ----------

    @Test
    void shouldReturnSuccessfullyListVideos() throws Exception {
        TestVideoContext context = createUserAndVideo("list-video@test.com",
                "icaro123", "IkratoListVideo", "listvideo/url/instagram",
                "Title", "https://youtube.com/watch?v=x", null, 1);

        mockMvc.perform(get("/video/list")
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].videoId").value(context.videoId().toString()))
                .andDo(print());
    }

    @Test
    void shouldReturnEmptyListWhenListVideosNoVideos() throws Exception {
        TestUserContext context = createUserAndProfile("list-video-empty@test.com",
                "icaro123", "IkratoListEmpty", "listempty/url/instagram");

        mockMvc.perform(get("/video/list")
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenListVideosNotToken() throws Exception {
        mockMvc.perform(get("/video/list"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/video/list"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenListVideosTokenInvalid() throws Exception {
        mockMvc.perform(get("/video/list")
                        .header("Authorization", "Bearer invalidTokenMeTest"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/video/list"))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenListVideosProfileNotFound() throws Exception {
        String token = createUserAndLogin("list-video-no-profile@test.com", "icaro123");

        mockMvc.perform(get("/video/list")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profile not found"))
                .andExpect(jsonPath("$.path").value("/video/list"))
                .andDo(print());
    }
}