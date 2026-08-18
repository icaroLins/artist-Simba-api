package com.icaro.simba_dj.controller;

import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class EventControllerTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;


    @Test
    void shouldReturnSuccessFullyCreateEvent() throws Exception {
        TestUserContext context = createUserAndProfile("teste-event-create@test.com",
                    "icaro123",
                    "test-event-create",
                    "instagram/event/url");

        User user = userRepository.findByUserEmail("teste-event-create@test.com");
        user.setEmailVerified(true);
        userRepository.save(user);

        LocalDate date = LocalDate.of(2026, 8, 1);

        String json = eventJson("test-event-create",
                date, "tese/url/link/event/ticket", "Gama-df");

        mockMvc.perform(post("/event/create")
                .header("Authorization", "Bearer " + context.token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventTitle").value("test-event-create"))
                .andExpect(jsonPath("$.eventId").exists())
                .andDo(print());

    }

    @Test
    void sholdReturnUnauthorizedForCreateEventNotToken() throws Exception {
        createUserAndProfile("teste-event-create-not-token@test.com",
                "icaro123",
                "test-event-create",
                "instagram/event/url");

        User user = userRepository.findByUserEmail("teste-event-create-not-token@test.com");
        user.setEmailVerified(true);
        userRepository.save(user);

        LocalDate date = LocalDate.of(2026, 8, 1);

        String json = eventJson("test-event-create",
                date, "tese/url/link/event/ticket", "Gama-df");

        mockMvc.perform(post("/event/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/event/create"))
                .andDo(print());

    }

    @Test
    void shouldReturnUnauthorizedForCreateEventTokenInvalid() throws Exception {
        createUserAndProfile("teste-event-create-ivalid-token@test.com",
                "icaro123",
                "test-event-create",
                "instagram/event/url");

        User user = userRepository.findByUserEmail("teste-event-create-ivalid-token@test.com");
        user.setEmailVerified(true);
        userRepository.save(user);

        LocalDate date = LocalDate.of(2026, 8, 1);

        String json = eventJson("test-event-create",
                date, "tese/url/link/event/ticket", "Gama-df");

        mockMvc.perform(post("/event/create")
                        .header("Authorization", "Bearer " + "token-ivalid-me-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/event/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnBadRequestForCreateEventDateNull() throws Exception {
        TestUserContext context = createUserAndProfile("teste-event-create-null-date@test.com",
                "icaro123",
                "test-event-create",
                "instagram/event/url");

        User user = userRepository.findByUserEmail("teste-event-create-null-date@test.com");
        user.setEmailVerified(true);
        userRepository.save(user);

        String json = eventJson("Test-Date",
                null, "tese/url/link/event/ticket", "Gama-df");

        mockMvc.perform(post("/event/create")
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("eventDate: Date is required"))
                .andExpect(jsonPath("$.path").value("/event/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnBadRequestForCreateEventTicketLinkNull() throws Exception {
        TestUserContext context = createUserAndProfile("teste-event-create-null-ticket@test.com",
                "icaro123",
                "test-event-create",
                "instagram/event/url");

        User user = userRepository.findByUserEmail("teste-event-create-null-ticket@test.com");
        user.setEmailVerified(true);
        userRepository.save(user);

        LocalDate date = LocalDate.of(2026, 8, 1);

        String json = eventJson("Test-Ticket",
                date, null, "Gama-df");

        mockMvc.perform(post("/event/create")
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("eventTicketLink: Ticket link is required"))
                .andExpect(jsonPath("$.path").value("/event/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnBadRequestForCreateEventLocationNull() throws Exception {
        TestUserContext context = createUserAndProfile("teste-event-create-null-location@test.com",
                "icaro123",
                "test-event-create",
                "instagram/event/url");

        User user = userRepository.findByUserEmail("teste-event-create-null-location@test.com");
        user.setEmailVerified(true);
        userRepository.save(user);

        LocalDate date = LocalDate.of(2026, 8, 1);

        String json = eventJson("Test-Location",
                date, "tese/url/link/event/ticket", null);

        mockMvc.perform(post("/event/create")
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("eventLocation: Location is required"))
                .andExpect(jsonPath("$.path").value("/event/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnBadRequestForCreateEventTitleNull() throws Exception {
        TestUserContext context = createUserAndProfile("teste-event-create-null-title@test.com",
                "icaro123",
                "test-event-create",
                "instagram/event/url");

        User user = userRepository.findByUserEmail("teste-event-create-null-title@test.com");
        user.setEmailVerified(true);
        userRepository.save(user);

        LocalDate date = LocalDate.of(2026, 8, 1);

        String json = eventJson(null,
                date, "tese/url/link/event/ticket", "Gama-df");

        mockMvc.perform(post("/event/create")
                        .header("Authorization", "Bearer " + context.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("eventTitle: Title is required"))
                .andExpect(jsonPath("$.path").value("/event/create"))
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessFullyWhenEventUpdate() throws Exception {

        TestUserContext ctxUser = createUserAndProfile("teste-event-update@test.com",
                "icaro123",
                "test-event-update",
                "instagram/event/url");



        LocalDate date = LocalDate.of(2026, 8, 1);
        LocalDate dateNew = LocalDate.of(2027, 6, 1);

        TestEventContext ctxEvent = createEvent("Title-event", date, "Teste/link/update", "Test-location-update", ctxUser);

        String json = eventJson("New-Test-Title",
                dateNew, "tese/url/new/link/event/ticket", "new-df");

        mockMvc.perform(patch("/event/edit/" + ctxEvent.eventId())
                        .header("Authorization", "Bearer " + ctxEvent.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventTitle").value("New-Test-Title"))
                .andExpect(jsonPath("$.eventDate").value(dateNew.toString()))
                .andExpect(jsonPath("$.eventTicketLink").value("tese/url/new/link/event/ticket"))
                .andExpect(jsonPath("$.eventLocation").value("new-df"))
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessFullyWhenEventUpdateTitleNull() throws Exception {
        TestUserContext ctxUser = createUserAndProfile("teste-event-update-title--null@test.com",
                "icaro123",
                "test-event-update",
                "instagram/event/url");



        LocalDate date = LocalDate.of(2026, 8, 1);
        LocalDate dateNew = LocalDate.of(2027, 6, 1);

        TestEventContext ctxEvent = createEvent("Title-event", date, "Teste/link/update", "Test-location-update", ctxUser);

        String json = eventJson(null,
                dateNew, "tese/url/new/link/event/ticket", "new-df");

        mockMvc.perform(patch("/event/edit/" + ctxEvent.eventId())
                        .header("Authorization", "Bearer " + ctxEvent.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventTitle").value("Title-event"))
                .andExpect(jsonPath("$.eventDate").value(dateNew.toString()))
                .andExpect(jsonPath("$.eventTicketLink").value("tese/url/new/link/event/ticket"))
                .andExpect(jsonPath("$.eventLocation").value("new-df"))
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessFullyWhenEventUpdateDateNull() throws Exception {
        TestUserContext ctxUser = createUserAndProfile("teste-event-update-date-null@test.com",
                "icaro123",
                "test-event-update",
                "instagram/event/url");



        LocalDate date = LocalDate.of(2026, 8, 1);

        TestEventContext ctxEvent = createEvent("Title-event", date, "Teste/link/update", "Test-location-update", ctxUser);

        String json = eventJson("Title-new",
                null, "tese/url/new/link/event/ticket", "new-df");

        mockMvc.perform(patch("/event/edit/" + ctxEvent.eventId())
                        .header("Authorization", "Bearer " + ctxEvent.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventTitle").value("Title-new"))
                .andExpect(jsonPath("$.eventDate").value(date.toString()))
                .andExpect(jsonPath("$.eventTicketLink").value("tese/url/new/link/event/ticket"))
                .andExpect(jsonPath("$.eventLocation").value("new-df"))
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessFullyWhenEventUpdateTicketNull() throws Exception {
        TestUserContext ctxUser = createUserAndProfile("teste-event-update-ticket-null@test.com",
                "icaro123",
                "test-event-update",
                "instagram/event/url");



        LocalDate date = LocalDate.of(2026, 8, 1);
        LocalDate dateNew = LocalDate.of(2027, 6, 1);

        TestEventContext ctxEvent = createEvent("Title-event", date, "Teste/link/update", "Test-location-update", ctxUser);

        String json = eventJson("Title-new",
                dateNew, null, "new-df");

        mockMvc.perform(patch("/event/edit/" + ctxEvent.eventId())
                        .header("Authorization", "Bearer " + ctxEvent.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventTitle").value("Title-new"))
                .andExpect(jsonPath("$.eventDate").value(dateNew.toString()))
                .andExpect(jsonPath("$.eventTicketLink").value("Teste/link/update"))
                .andExpect(jsonPath("$.eventLocation").value("new-df"))
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessFullyWhenEventUpdateLocationNull() throws Exception {
        TestUserContext ctxUser = createUserAndProfile("teste-event-update-location-null@test.com",
                "icaro123",
                "test-event-update",
                "instagram/event/url");



        LocalDate date = LocalDate.of(2026, 8, 1);
        LocalDate dateNew = LocalDate.of(2027, 6, 1);

        TestEventContext ctxEvent = createEvent("Title-event", date, "Teste/link/update", "Test-location-update", ctxUser);

        String json = eventJson("Title-new",
                dateNew, "tese/url/new/link/event/ticket", null);

        mockMvc.perform(patch("/event/edit/" + ctxEvent.eventId())
                        .header("Authorization", "Bearer " + ctxEvent.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventTitle").value("Title-new"))
                .andExpect(jsonPath("$.eventDate").value(dateNew.toString()))
                .andExpect(jsonPath("$.eventTicketLink").value("tese/url/new/link/event/ticket"))
                .andExpect(jsonPath("$.eventLocation").value("Test-location-update"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenEventUpdateNotToken() throws Exception {
        TestUserContext ctxUser = createUserAndProfile("teste-event-update-not-token@test.com",
                "icaro123",
                "test-event-update",
                "instagram/event/url");



        LocalDate date = LocalDate.of(2026, 8, 1);
        LocalDate dateNew = LocalDate.of(2027, 6, 1);

        TestEventContext ctxEvent = createEvent("Title-event", date, "Teste/link/update", "Test-location-update", ctxUser);

        String json = eventJson("New-Test-Title",
                dateNew, "tese/url/new/link/event/ticket", "new-df");

        mockMvc.perform(patch("/event/edit/" + ctxEvent.eventId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/event/edit/"+ ctxEvent.eventId()))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenEventUpdateTokenInvalid() throws Exception {
        TestUserContext ctxUser = createUserAndProfile("teste-event-update-token-invalid@test.com",
                "icaro123",
                "test-event-update",
                "instagram/event/url");



        LocalDate date = LocalDate.of(2026, 8, 1);
        LocalDate dateNew = LocalDate.of(2027, 6, 1);

        TestEventContext ctxEvent = createEvent("Title-event", date, "Teste/link/update", "Test-location-update", ctxUser);

        String json = eventJson("New-Test-Title",
                dateNew, "tese/url/new/link/event/ticket", "new-df");

        mockMvc.perform(patch("/event/edit/" + ctxEvent.eventId())
                .header("Authorization", "Bearer " + "token-invalid-test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/event/edit/"+ ctxEvent.eventId()))
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessFullyWhenEventList() throws Exception {
        TestUserContext ctxUser = createUserAndProfile("teste-event-get@test.com",
                "icaro123",
                "test-event-get",
                "instagram/event/url");



        LocalDate date = LocalDate.of(2026, 8, 1);
        LocalDate dateTwo = LocalDate.of(2027, 6, 1);

        createEvent("Title-event", date, "Teste/link/update", "Test-location-update", ctxUser);
        createEvent("Title-event-two", dateTwo, "Teste/link/update/two", "Test-location-update-two", ctxUser);

        mockMvc.perform(get("/event/get")
                        .header("Authorization", "Bearer " + ctxUser.token()))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    void shouldReturnUnauthorizedWhenEventGetNotToken() throws Exception {
        TestUserContext ctxUser = createUserAndProfile("teste-event-get-token-not@test.com",
                "icaro123",
                "test-event-get",
                "instagram/event/url");



        LocalDate date = LocalDate.of(2026, 8, 1);
        LocalDate dateTwo = LocalDate.of(2027, 6, 1);

        createEvent("Title-event", date, "Teste/link/update", "Test-location-update", ctxUser);
        createEvent("Title-event-two", dateTwo, "Teste/link/update/two", "Test-location-update-two", ctxUser);

        mockMvc.perform(get("/event/get"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/event/get"))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenEventGetTokenInvalid() throws Exception {
        TestUserContext ctxUser = createUserAndProfile("teste-event-get-token-invalid@test.com",
                "icaro123",
                "test-event-get",
                "instagram/event/url");



        LocalDate date = LocalDate.of(2026, 8, 1);
        LocalDate dateTwo = LocalDate.of(2027, 6, 1);

        createEvent("Title-event", date, "Teste/link/update", "Test-location-update", ctxUser);
        createEvent("Title-event-two", dateTwo, "Teste/link/update/two", "Test-location-update-two", ctxUser);

        mockMvc.perform(get("/event/get")
                        .header("Authorization", "Bearer " + "token-invalid-test"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/event/get"))
                .andDo(print());
    }

    @Test
    void shouldReturnSuccessfullyWhenEventDelete() throws Exception {
        TestUserContext ctxUser = createUserAndProfile("teste-event-delete@test.com",
                "icaro123",
                "test-event-get",
                "instagram/event/url");

        LocalDate date = LocalDate.of(2026, 8, 1);

        TestEventContext ctxEvent = createEvent("Title-event", date, "Teste/link/update", "Test-location-update", ctxUser);

        mockMvc.perform(delete("/event/delete/"+ ctxEvent.eventId())
                        .header("Authorization", "Bearer " + ctxUser.token()))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    void shouldReturnUnauthorizedWhenEventDeleteNotToken() throws Exception {
        TestUserContext ctxUser = createUserAndProfile("teste-event-delete-not-token@test.com",
                "icaro123",
                "test-event-get",
                "instagram/event/url");

        LocalDate date = LocalDate.of(2026, 8, 1);

        TestEventContext ctxEvent = createEvent("Title-event", date, "Teste/link/update", "Test-location-update", ctxUser);

        mockMvc.perform(delete("/event/delete/"+ ctxEvent.eventId()))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/event/delete/" +ctxEvent.eventId()))
                .andDo(print());
    }

    @Test
    void shouldReturnUnauthorizedWhenEventDeleteTokenInvalid() throws Exception {
        TestUserContext ctxUser = createUserAndProfile("teste-event-delete-ivalid-token@test.com",
                "icaro123",
                "test-event-get",
                "instagram/event/url");

        LocalDate date = LocalDate.of(2026, 8, 1);

        TestEventContext ctxEvent = createEvent("Title-event", date, "Teste/link/update", "Test-location-update", ctxUser);

        mockMvc.perform(delete("/event/delete/"+ ctxEvent.eventId())
                        .header("Authorization", "Bearer " + "token-invalid-test"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required."))
                .andExpect(jsonPath("$.path").value("/event/delete/" +ctxEvent.eventId()))
                .andDo(print());
    }

}
