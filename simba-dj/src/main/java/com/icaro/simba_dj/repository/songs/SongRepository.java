package com.icaro.simba_dj.repository.songs;

import com.icaro.simba_dj.enums.ContentStatus;
import com.icaro.simba_dj.model.songs.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SongRepository extends JpaRepository<Song, UUID> {
    List<Song> findByProfileProfileIdAndSongStatus(UUID profileId, ContentStatus songStatus);
    Optional<Song> findBySongIdAndProfileProfileIdAndSongStatus(UUID songId, UUID profileId, ContentStatus songStatus);
    boolean existsByShortlink(String shortlink);
}
