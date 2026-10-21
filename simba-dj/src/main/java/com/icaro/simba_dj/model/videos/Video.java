package com.icaro.simba_dj.model.videos;

import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.model.profiles.Profile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="video_tb")
@Getter
@Setter
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "video_id", updatable = false, nullable = false)
    private UUID videoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_profiles_id", nullable = false)
    @NotNull
    private Profile profile;

    @NotBlank
    @Column(name = "video_title", nullable = false)
    private String title;

    @NotBlank
    @Column(name = "video_url", nullable = false)
    private String url;

    @Column(name = "video_thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "video_display_order")
    private Integer displayOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "VideoStatus", nullable = false)
    private ContentStatus videoStatus;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.videoStatus == null) {
            this.videoStatus = ContentStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
