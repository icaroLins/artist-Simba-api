package com.icaro.simba_dj.model.email;

import com.icaro.simba_dj.model.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="email_verification_tokens_tb")
@Getter @Setter
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }
}
