package com.icaro.simba_dj.model.songs;

import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.model.plataformLink.SongPlatformLink;
import com.icaro.simba_dj.model.profiles.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="song_tb")
@Getter
@Setter
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID songId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String coverUrl;

    @Column(nullable = false)
    private Integer displayOrder;

    @Column(nullable = false, unique = true)
    private String shortlink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus songStatus;

    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SongPlatformLink> platformLinks = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
