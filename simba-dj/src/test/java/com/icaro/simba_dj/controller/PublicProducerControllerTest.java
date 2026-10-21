package com.icaro.simba_dj.controller;

import com.icaro.simba_dj.dto.request.platformLink.PlatformLinkRequest;
import com.icaro.simba_dj.enums.MusicPlatform;
import com.icaro.simba_dj.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicProducerControllerTest extends AbstractIntegrationTest {

    private List<PlatformLinkRequest> defaultLinks() {
        return List.of(
                new PlatformLinkRequest(MusicPlatform.SPOTIFY, "https://open.spotify.com/track/public"),
                new PlatformLinkRequest(MusicPlatform.YOUTUBE, "https://youtube.com/watch?v=public")
        );
    }

    @Test
    void shouldReturnPublicProducerWithoutToken() throws Exception {
        TestUserContext context = createUserAndProfile(
                "public-producer@test.com",
                "icaro123",
                "Simba",
                "https://instagram.com/simba"
        );

        String songBody = songJson("Public Song", "https://covers.example/public-song.png", 1, defaultLinks());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/song/create")
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(songBody))
                .andExpect(status().isOk())
                .andDo(print());

        String photoBody = photoJson(1);
        MockMultipartFile photoPart = new MockMultipartFile(
                "photo", "",
                MediaType.APPLICATION_JSON_VALUE,
                photoBody.getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile imagePart = new MockMultipartFile(
                "photoImage",
                "public-photo.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes(StandardCharsets.UTF_8)
        );
        mockMvc.perform(multipart("/photo/create")
                        .file(photoPart)
                        .file(imagePart)
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andDo(print());

        String videoBody = videoJson(
                "Public Video",
                "https://youtube.com/watch?v=public-video",
                "https://img.youtube.com/vi/public-video/hqdefault.jpg",
                1
        );
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/video/create")
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(videoBody))
                .andExpect(status().isOk())
                .andDo(print());

        LocalDate eventDate = LocalDate.of(2026, 9, 1);
        String eventBody = eventJson(
                "Public Event",
                eventDate,
                "https://tickets.example/public-event",
                "Brasilia"
        );
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/event/create")
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventBody))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/public/producer/simba"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.profileShortlink").value("simba"))
                .andExpect(jsonPath("$.profile.profileNameArtist").value("Simba"))
                .andExpect(jsonPath("$.songs.length()").value(1))
                .andExpect(jsonPath("$.photos.length()").value(1))
                .andExpect(jsonPath("$.videos.length()").value(1))
                .andExpect(jsonPath("$.events.length()").value(1))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenPublicProducerDoesNotExist() throws Exception {
        mockMvc.perform(get("/public/producer/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Producer not found"))
                .andExpect(jsonPath("$.path").value("/public/producer/unknown"))
                .andDo(print());
    }
}
