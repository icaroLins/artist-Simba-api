package com.icaro.simba_dj.repository.passwordReset;

import com.icaro.simba_dj.model.passwordReset.PasswordResetToken;
import com.icaro.simba_dj.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
}

