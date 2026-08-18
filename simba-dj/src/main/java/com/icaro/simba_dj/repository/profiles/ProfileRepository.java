package com.icaro.simba_dj.repository.profiles;

import com.icaro.simba_dj.model.profiles.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByUserUserId(UUID id);
    Optional<Profile> findByProfileShortlink(String profileShortlink);
    boolean existsByProfileShortlink(String profileShortlink);
}
