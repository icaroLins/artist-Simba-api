package com.icaro.simba_dj.repository.email;

import com.icaro.simba_dj.model.email.EmailVerificationToken;
import com.icaro.simba_dj.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerificationToken, UUID> {
    Optional<EmailVerificationToken> findByToken(String token);
    void deleteByUser(User user);

    Optional<EmailVerificationToken> findByUser(User user);
}
