package com.icaro.simba_dj.model.events;

import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.model.profiles.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "Event_id")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID eventId;

    @ManyToOne
    @JoinColumn(name = "profileId", nullable = false)
    private Profile profile;

    @Column(nullable = false)
    private String eventTitle;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false)
    private String eventTicketLink;

    @Column(nullable = false)
    private String eventLocation;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime eventCreateAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus eventStatus = ContentStatus.ACTIVE;
}
