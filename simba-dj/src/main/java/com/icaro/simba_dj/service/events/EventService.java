package com.icaro.simba_dj.service.events;

import com.icaro.simba_dj.dto.request.events.EventRequest;
import com.icaro.simba_dj.dto.response.events.EventResponse;
import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.exception.BusinessException;
import com.icaro.simba_dj.exception.ForbiddenException;
import com.icaro.simba_dj.exception.ResourceNotFoundException;
import com.icaro.simba_dj.mappers.events.EventMapper;
import com.icaro.simba_dj.model.events.Event;
import com.icaro.simba_dj.model.profiles.Profile;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.repository.events.EventRepository;
import com.icaro.simba_dj.repository.profiles.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private ProfileRepository profileRepository;

    public EventResponse createEvent(EventRequest eventRequest, User user) {

        Optional<Profile> profile = profileRepository.findByUserUserId(user.getUserId());

        Event event = eventMapper.toEntity(eventRequest);
        event.setProfile(profile.orElseThrow(() -> new ResourceNotFoundException("Profile not found")));
        eventRepository.save(event);
        return eventMapper.toResponse(event);
    }

    @Transactional
    public EventResponse patchEvent(EventRequest eventRequest, UUID eventId, User user) {

        Profile profile = profileRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        Event event =  eventRepository
                .findByEventIdAndProfileProfileId(eventId, profile.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (eventRequest.eventDate() != null) {
            event.setEventDate(eventRequest.eventDate());
        }

        if (eventRequest.eventTitle() != null) {
            event.setEventTitle(eventRequest.eventTitle());
        }

        if (eventRequest.eventLocation() != null) {
            event.setEventLocation(eventRequest.eventLocation());
        }

        if (eventRequest.eventTicketLink() != null) {
            event.setEventTicketLink(eventRequest.eventTicketLink());
        }

        return eventMapper.toResponse(event);
    }

    public List<EventResponse> getEvent(User user){
        Profile profile = profileRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        return eventRepository.findByProfileProfileIdAndEventStatus(profile.getProfileId(), ContentStatus.ACTIVE)
                .stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    public EventResponse deleteEvent(UUID eventId, User user){
        Profile profile = profileRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if(!event.getProfile().getProfileId().equals(profile.getProfileId())) {
            throw new RuntimeException("Profile Id not match");
        }

        if(event.getEventStatus() == ContentStatus.INACTIVE){
            throw new BusinessException("Event inactivated");
        }

        event.setEventStatus(ContentStatus.INACTIVE);
        eventRepository.save(event);
        return eventMapper.toResponse(event);
    }
}
