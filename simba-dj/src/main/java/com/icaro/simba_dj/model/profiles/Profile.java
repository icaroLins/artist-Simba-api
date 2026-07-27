package com.icaro.simba_dj.model.profiles;

import com.icaro.simba_dj.model.plataformLink.SongPlatformLink;
import com.icaro.simba_dj.model.songs.Song;
import com.icaro.simba_dj.model.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "profile_tb")
@Getter
@Setter
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID profileId;

    @Column(nullable = false)
    private String profileNameArtist;


    private String profilePhotoUrl;

    private String profileInstagramUrl;

    @OneToOne
    @JoinColumn(name = "userId", unique = true)
    private User user;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Song> songs = new ArrayList<>();
}
