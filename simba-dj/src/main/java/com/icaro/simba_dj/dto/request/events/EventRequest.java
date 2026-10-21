package com.icaro.simba_dj.dto.request.events;

import com.icaro.simba_dj.model.profiles.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EventRequest(
        @NotNull(message = "Title is required")
        String eventTitle,
        @NotNull(message = "Date is required")
        LocalDate eventDate,
        @NotBlank(message = "Ticket link is required")
        String eventTicketLink,
        @NotBlank(message = "Location is required")
        String eventLocation
) {
}
