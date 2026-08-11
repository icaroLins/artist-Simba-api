package com.icaro.simba_dj.controller;


import com.icaro.simba_dj.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PhotoControllerTest extends AbstractIntegrationTest {

    @Test
    void shouldReturnSuccessfullyCreatedPhoto() throws Exception {
        TestUserContext context = createUserAndProfile("create-photo@test.com",
                "icaro123", "Ikrato", "cratephoto/url/instagram");

        String json = photoJson(1);

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

        mockMvc.perform(multipart("/photo/create")
                        .file(photoPart)
                        .file(imagePart)
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoId").exists())
                .andExpect(jsonPath("$.url").exists())
                .andExpect(jsonPath("$.displayOrder").value(1))
                .andExpect(jsonPath("$.photoStatus").value("ACTIVE"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenCreatePhotoNotToken() throws Exception {
        String json = photoJson(1);

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

        mockMvc.perform(multipart("/photo/create")
                        .file(photoPart)
                        .file(imagePart))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/photo/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenCreatePhotoTokenInvalid() throws Exception {
        String json = photoJson(1);

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

        mockMvc.perform(multipart("/photo/create")
                        .file(photoPart)
                        .file(imagePart)
                        .header("Authorization", "Bearer token.invalid.test"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/photo/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenCreatePhotoProfileNotFound() throws Exception {
        String token = createUserAndLogin("create-photo-no-profile@test.com", "icaro123");
        String json = photoJson(1);

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

        mockMvc.perform(multipart("/photo/create")
                        .file(photoPart)
                        .file(imagePart)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profile not found"))
                .andExpect(jsonPath("$.path").value("/photo/create"))
                .andDo(print());
    }

    // OBS: hoje isso retorna 500 porque MissingServletRequestPartException
    // não é RuntimeException e cai no handler genérico. Documentando o comportamento atual.
    @Test
    void shouldReturnInternalServerErrorWhenCreatePhotoImageMissing() throws Exception {
        TestUserContext context = createUserAndProfile("create-photo-no-image@test.com",
                "icaro123", "IkratoNoImage", "noimage/url/instagram");

        String json = photoJson(1);

        MockMultipartFile photoPart = new MockMultipartFile(
                "photo", "", MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/photo/create")
                        .file(photoPart)
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Erro interno no servidor"))
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessWhenUpdatePhoto() throws Exception {
        TestPhotoContext context = createUserAndPhoto("update-photo@test.com",
                "icaro123", "IkratoUpdate", "update/url/instagram", 1);

        String updatedJson = photoJson(2);

        MockMultipartFile photoPart = new MockMultipartFile(
                "photo", "", MediaType.APPLICATION_JSON_VALUE,
                updatedJson.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "file",
                "test-photo-updated.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content-updated".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart(HttpMethod.PATCH, "/photo/update/" + context.photoId())
                        .file(photoPart)
                        .file(imagePart)
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoId").value(context.photoId().toString()))
                .andExpect(jsonPath("$.displayOrder").value(2))
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessWhenUpdatePhotoImageNull() throws Exception {
        TestPhotoContext context = createUserAndPhoto("update-photo-no-image@test.com",
                "icaro123", "IkratoUpdateNoImage", "update-no-image/url/instagram", 1);

        String updatedJson = photoJson(3);

        MockMultipartFile photoPart = new MockMultipartFile(
                "photo", "", MediaType.APPLICATION_JSON_VALUE,
                updatedJson.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart(HttpMethod.PATCH, "/photo/update/" + context.photoId())
                        .file(photoPart)
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayOrder").value(3))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenUpdatePhotoNotToken() throws Exception {
        TestPhotoContext context = createUserAndPhoto("update-photo-unauthorized@test.com",
                "icaro123", "IkratoUpdateUnauth", "update-unauth/url/instagram", 1);

        String updatedJson = photoJson(2);

        MockMultipartFile photoPart = new MockMultipartFile(
                "photo", "", MediaType.APPLICATION_JSON_VALUE,
                updatedJson.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart(HttpMethod.PATCH, "/photo/update/" + context.photoId())
                        .file(photoPart))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/photo/update/" + context.photoId()))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenUpdatePhotoTokenInvalid() throws Exception {
        TestPhotoContext context = createUserAndPhoto("update-photo-invalid-token@test.com",
                "icaro123", "IkratoUpdateInvalid", "update-invalid/url/instagram", 1);

        String updatedJson = photoJson(2);

        MockMultipartFile photoPart = new MockMultipartFile(
                "photo", "", MediaType.APPLICATION_JSON_VALUE,
                updatedJson.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart(HttpMethod.PATCH, "/photo/update/" + context.photoId())
                        .file(photoPart)
                        .header("Authorization", "Bearer invalidTokenMeTest"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/photo/update/" + context.photoId()))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatePhotoPhotoNotFound() throws Exception {
        TestUserContext context = createUserAndProfile("update-photo-not-found@test.com",
                "icaro123", "IkratoUpdateNotFound", "update-not-found/url/instagram");

        UUID randomPhotoId = UUID.randomUUID();
        String updatedJson = photoJson(2);

        MockMultipartFile photoPart = new MockMultipartFile(
                "photo", "", MediaType.APPLICATION_JSON_VALUE,
                updatedJson.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart(HttpMethod.PATCH, "/photo/update/" + randomPhotoId)
                        .file(photoPart)
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Photo not found"))
                .andExpect(jsonPath("$.path").value("/photo/update/" + randomPhotoId))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatePhotoProfileNotFound() throws Exception {
        String token = createUserAndLogin("update-photo-no-profile@test.com", "icaro123");
        UUID randomPhotoId = UUID.randomUUID();
        String updatedJson = photoJson(2);

        MockMultipartFile photoPart = new MockMultipartFile(
                "photo", "", MediaType.APPLICATION_JSON_VALUE,
                updatedJson.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart(HttpMethod.PATCH, "/photo/update/" + randomPhotoId)
                        .file(photoPart)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profile not found"))
                .andExpect(jsonPath("$.path").value("/photo/update/" + randomPhotoId))
                .andDo(print());
    }


    @Test
    void shouldReturnSuccessWhenDeletePhoto() throws Exception {
        TestPhotoContext context = createUserAndPhoto("delete-photo@test.com",
                "icaro123", "IkratoDelete", "delete/url/instagram", 1);

        mockMvc.perform(delete("/photo/delete/" + context.photoId())
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/photo/list")
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenDeletePhotoNotToken() throws Exception {
        TestPhotoContext context = createUserAndPhoto("delete-photo-unauthorized@test.com",
                "icaro123", "IkratoDeleteUnauth", "delete-unauth/url/instagram", 1);

        mockMvc.perform(delete("/photo/delete/" + context.photoId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/photo/delete/" + context.photoId()))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenDeletePhotoTokenInvalid() throws Exception {
        TestPhotoContext context = createUserAndPhoto("delete-photo-invalid-token@test.com",
                "icaro123", "IkratoDeleteInvalid", "delete-invalid/url/instagram", 1);

        mockMvc.perform(delete("/photo/delete/" + context.photoId())
                        .header("Authorization", "Bearer invalidTokenMeTest"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/photo/delete/" + context.photoId()))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenDeletePhotoPhotoNotFound() throws Exception {
        TestUserContext context = createUserAndProfile("delete-photo-not-found@test.com",
                "icaro123", "IkratoDeleteNotFound", "delete-not-found/url/instagram");

        UUID randomPhotoId = UUID.randomUUID();

        mockMvc.perform(delete("/photo/delete/" + randomPhotoId)
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Photo not found"))
                .andExpect(jsonPath("$.path").value("/photo/delete/" + randomPhotoId))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenDeletePhotoProfileNotFound() throws Exception {
        String token = createUserAndLogin("delete-photo-no-profile@test.com", "icaro123");
        UUID randomPhotoId = UUID.randomUUID();

        mockMvc.perform(delete("/photo/delete/" + randomPhotoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profile not found"))
                .andExpect(jsonPath("$.path").value("/photo/delete/" + randomPhotoId))
                .andDo(print());
    }

    // ---------- LIST ----------

    @Test
    void shouldReturnSuccessfullyListPhotos() throws Exception {
        TestPhotoContext context = createUserAndPhoto("list-photo@test.com",
                "icaro123", "IkratoList", "list/url/instagram", 1);

        mockMvc.perform(get("/photo/list")
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].photoId").value(context.photoId().toString()))
                .andDo(print());
    }

    @Test
    void shouldReturnEmptyListWhenListPhotosNoPhotos() throws Exception {
        TestUserContext context = createUserAndProfile("list-photo-empty@test.com",
                "icaro123", "IkratoListEmpty", "list-empty/url/instagram");

        mockMvc.perform(get("/photo/list")
                        .header("Authorization", "Bearer " + context.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenListPhotosNotToken() throws Exception {
        mockMvc.perform(get("/photo/list"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/photo/list"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenListPhotosTokenInvalid() throws Exception {
        mockMvc.perform(get("/photo/list")
                        .header("Authorization", "Bearer invalidTokenMeTest"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/photo/list"))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundWhenListPhotosProfileNotFound() throws Exception {
        String token = createUserAndLogin("list-photo-no-profile@test.com", "icaro123");

        mockMvc.perform(get("/photo/list")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Profile not found"))
                .andExpect(jsonPath("$.path").value("/photo/list"))
                .andDo(print());
    }
}
