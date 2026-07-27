package com.icaro.simba_dj.mappers.events;

import com.icaro.simba_dj.dto.request.events.EventRequest;
import com.icaro.simba_dj.dto.response.events.EventResponse;
import com.icaro.simba_dj.model.events.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public Event toEntity(EventRequest eventRequest) {
        Event event = new Event();
        event.setEventDate(eventRequest.eventDate());
        event.setEventLocation(eventRequest.eventLocation());
        event.setEventTitle(eventRequest.eventTitle());
        event.setEventTicketLink(eventRequest.eventTicketLink());
        return event;
    }

    public EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getEventId(),
                event.getProfile().getProfileNameArtist(),
                event.getProfile().getProfileId(),
                event.getEventTitle(),
                event.getEventDate(),
                event.getEventTicketLink(),
                event.getEventLocation()
        );
    }
}
