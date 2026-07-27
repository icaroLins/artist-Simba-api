package com.icaro.simba_dj.model.photos;

import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.model.profiles.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="photo_tb")
@Getter
@Setter
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "photo_id", updatable = false, nullable = false)
    private UUID photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_profiles_id", nullable = false)
    private Profile profile;

    @Column(name = "photo_url", nullable = false, length = 500)
    private String url;

    @Column(name = "photo_display_order")
    private Integer displayOrder;

    @Column(name = "photo_create_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "photoStatus", nullable = false)
    private ContentStatus photoStatus;

    @Column(name = "photo_updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
