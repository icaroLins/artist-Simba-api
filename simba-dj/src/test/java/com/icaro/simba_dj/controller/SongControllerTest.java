package com.icaro.simba_dj.controller;

import com.icaro.simba_dj.dto.request.platformLink.PlatformLinkRequest;
import com.icaro.simba_dj.enums.MusicPlatform;
import com.icaro.simba_dj.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SongControllerTest extends AbstractIntegrationTest {

    private List<PlatformLinkRequest> defaultLinks() {
        return List.of(
                new PlatformLinkRequest(MusicPlatform.SPOTIFY, "https://open.spotify.com/track/abc"),
                new PlatformLinkRequest(MusicPlatform.YOUTUBE, "https://youtube.com/watch?v=abc")
        );
    }

    // ---------- CREATE ----------

    @Test
    void shouldReturnSuccessfullyCreatedSong() throws Exception {
        TestUserContext context = createUserAndProfile("create-song@test.com",
                "icaro123", "IkratoSong", "createsong/url/instagram");

        String json = songJson("Song Title", "http://cover.url/art.png", 1, defaultLinks());

        mockMvc.perform(post("/song/create")
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.songId").exists())
                .andExpect(jsonPath("$.title").value("Song Title"))
                .andExpect(jsonPath("$.displayOrder").value(1))
                .andExpect(jsonPath("$.platformLinks.length()").value(2))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenCreateSongNotToken() throws Exception {
        String json = songJson("Song Title", "http://cover.url/art.png", 1, defaultLinks());

        mockMvc.perform(post("/song/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/song/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenCreateSongTokenInvalid() throws Exception {
        String json = songJson("Song Title", "http://cover.url/art.png", 1, defaultLinks());

        mockMvc.perform(post("/song/create")
                        .header("Authorization", "Bearer token.invalid.test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/song/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenCreateSongProfileNotFound() throws Exception {
        String token = createUserAndLogin("create-song-no-profile@test.com", "icaro123");
        String json = songJson("Song Title", "http://cover.url/art.png", 1, defaultLinks());

        mockMvc.perform(post("/song/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profile not found"))
                .andExpect(jsonPath("$.path").value("/song/create"))
                .andDo(print());
    }

    // ---------- UPDATE ----------

    @Test
    void shouldReturnSuccessWhenUpdateSong() throws Exception {
        TestSongContext context = createUserAndSong("update-song@test.com",
                "icaro123", "IkratoUpdateSong", "updatesong/url/instagram",
                "Original Title", "http://cover.url/original.png", 1, defaultLinks());

        String updatedJson = songJson("New Title", "http://cover.url/new.png", 2,
                List.of(new PlatformLinkRequest(MusicPlatform.DEEZER, "https://deezer.com/track/xyz")));

        mockMvc.perform(patch("/song/update/" + context.songId())
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.songId").value(context.songId().toString()))
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.coverUrl").value("http://cover.url/new.png"))
                .andExpect(jsonPath("$.displayOrder").value(2))
                .andExpect(jsonPath("$.platformLinks.length()").value(1))
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessWhenUpdateSongTitleNullKeepsOriginal() throws Exception {
        TestSongContext context = createUserAndSong("update-song-title-null@test.com",
                "icaro123", "IkratoUpdateTitleNull", "updatetitlenull/url/instagram",
                "Original Title", "http://cover.url/original.png", 1, defaultLinks());

        String updatedJson = songJson(null, null, 5, List.of());

        mockMvc.perform(patch("/song/update/" + context.songId())
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Original Title"))
                .andExpect(jsonPath("$.coverUrl").value("http://cover.url/original.png"))
                .andExpect(jsonPath("$.displayOrder").value(5))
                .andExpect(jsonPath("$.platformLinks.length()").value(2))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenUpdateSongNotToken() throws Exception {
        TestSongContext context = createUserAndSong("update-song-unauthorized@test.com",
                "icaro123", "IkratoUpdateUnauth", "updateunauth/url/instagram",
                "Original Title", "http://cover.url/original.png", 1, defaultLinks());

        String updatedJson = songJson("New Title", "http://cover.url/new.png", 2, defaultLinks());

        mockMvc.perform(patch("/song/update/" + context.songId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/song/update/" + context.songId()))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenUpdateSongTokenInvalid() throws Exception {
        TestSongContext context = createUserAndSong("update-song-invalid-token@test.com",
                "icaro123", "IkratoUpdateInvalid", "updateinvalid/url/instagram",
                "Original Title", "http://cover.url/original.png", 1, defaultLinks());

        String updatedJson = songJson("New Title", "http://cover.url/new.png", 2, defaultLinks());

        mockMvc.perform(patch("/song/update/" + context.songId())
                        .header("Authorization", "Bearer invalidTokenMeTest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/song/update/" + context.songId()))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenUpdateSongSongNotFound() throws Exception {
        TestUserContext context = createUserAndProfile("update-song-not-found@test.com",
                "icaro123", "IkratoUpdateSongNotFound", "updatesongnotfound/url/instagram");

        UUID randomSongId = UUID.randomUUID();
        String updatedJson = songJson("New Title", "http://cover.url/new.png", 2, defaultLinks());

        mockMvc.perform(patch("/song/update/" + randomSongId)
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Song not found"))
                .andExpect(jsonPath("$.path").value("/song/update/" + randomSongId))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenUpdateSongProfileNotFound() throws Exception {
        String token = createUserAndLogin("update-song-no-profile@test.com", "icaro123");
        UUID randomSongId = UUID.randomUUID();
        String updatedJson = songJson("New Title", "http://cover.url/new.png", 2, defaultLinks());

        mockMvc.perform(patch("/song/update/" + randomSongId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profile not found"))
                .andExpect(jsonPath("$.path").value("/song/update/" + randomSongId))
                .andDo(print());
    }

    // ---------- DELETE ----------

    @Test
    void shouldReturnSuccessWhenDeleteSong() throws Exception {
        TestSongContext context = createUserAndSong("delete-song@test.com",
                "icaro123", "IkratoDeleteSong", "deletesong/url/instagram",
                "Song To Delete", "http://cover.url/delete.png", 1, defaultLinks());

        mockMvc.perform(delete("/song/delete/" + context.songId())
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/song/list")
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenDeleteSongNotToken() throws Exception {
        TestSongContext context = createUserAndSong("delete-song-unauthorized@test.com",
                "icaro123", "IkratoDeleteUnauth", "deleteunauth/url/instagram",
                "Song Title", "http://cover.url/x.png", 1, defaultLinks());

        mockMvc.perform(delete("/song/delete/" + context.songId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/song/delete/" + context.songId()))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenDeleteSongTokenInvalid() throws Exception {
        TestSongContext context = createUserAndSong("delete-song-invalid-token@test.com",
                "icaro123", "IkratoDeleteInvalid", "deleteinvalid/url/instagram",
                "Song Title", "http://cover.url/x.png", 1, defaultLinks());

        mockMvc.perform(delete("/song/delete/" + context.songId())
                        .header("Authorization", "Bearer invalidTokenMeTest"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/song/delete/" + context.songId()))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteSongSongNotFound() throws Exception {
        TestUserContext context = createUserAndProfile("delete-song-not-found@test.com",
                "icaro123", "IkratoDeleteSongNotFound", "deletesongnotfound/url/instagram");

        UUID randomSongId = UUID.randomUUID();

        mockMvc.perform(delete("/song/delete/" + randomSongId)
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Song not found"))
                .andExpect(jsonPath("$.path").value("/song/delete/" + randomSongId))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteSongProfileNotFound() throws Exception {
        String token = createUserAndLogin("delete-song-no-profile@test.com", "icaro123");
        UUID randomSongId = UUID.randomUUID();

        mockMvc.perform(delete("/song/delete/" + randomSongId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profile not found"))
                .andExpect(jsonPath("$.path").value("/song/delete/" + randomSongId))
                .andDo(print());
    }

    // ---------- LIST ----------

    @Test
    void shouldReturnSuccessfullyListSongs() throws Exception {
        TestSongContext context = createUserAndSong("list-song@test.com",
                "icaro123", "IkratoListSong", "listsong/url/instagram",
                "Song Title", "http://cover.url/x.png", 1, defaultLinks());

        mockMvc.perform(get("/song/list")
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].songId").value(context.songId().toString()))
                .andDo(print());
    }

    @Test
    void shouldReturnEmptyListWhenListSongsNoSongs() throws Exception {
        TestUserContext context = createUserAndProfile("list-song-empty@test.com",
                "icaro123", "IkratoListEmpty", "listempty/url/instagram");

        mockMvc.perform(get("/song/list")
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenListSongsNotToken() throws Exception {
        mockMvc.perform(get("/song/list"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/song/list"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenListSongsTokenInvalid() throws Exception {
        mockMvc.perform(get("/song/list")
                        .header("Authorization", "Bearer invalidTokenMeTest"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/song/list"))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenListSongsProfileNotFound() throws Exception {
        String token = createUserAndLogin("list-song-no-profile@test.com", "icaro123");

        mockMvc.perform(get("/song/list")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profile not found"))
                .andExpect(jsonPath("$.path").value("/song/list"))
                .andDo(print());
    }
}