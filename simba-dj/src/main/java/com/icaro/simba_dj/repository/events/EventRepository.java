package com.icaro.simba_dj.repository.events;

import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.model.events.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByProfileProfileId(UUID eventProfileId);
    List<Event> findByEventStatus(ContentStatus eventEventStatus);
    List<Event> findByProfileProfileIdAndEventStatus(UUID profileId, ContentStatus status);

    Optional<Event> findByEventIdAndProfileProfileId(UUID eventId, UUID profileId);
}
