package com.icaro.simba_dj.model.plataformLink;

import com.icaro.simba_dj.enums.MusicPlatform;
import com.icaro.simba_dj.model.songs.Song;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="plataform_link_tb")
@Getter
@Setter
public class SongPlatformLink {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID plataformLinkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MusicPlatform platform;

    @Column(nullable = false)
    private String externalLink;
}
