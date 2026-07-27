package com.icaro.simba_dj.dto.response.events;

import java.time.LocalDate;
import java.util.UUID;

public record EventResponse (
        UUID eventId,
        String profileNameArtist,
        UUID profileId,
        String eventTitle,
        LocalDate eventDate,
        String eventTicketLink,
        String eventLocation
){}
