package com.icaro.simba_dj.model.users;

import com.icaro.simba_dj.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_tb")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(unique = true)
    private String userEmail;

    @Column(nullable = false)
    private String userPassword_hash;

    @CreationTimestamp
            @Column(nullable = false,updatable = false)
    private LocalDateTime userCreatedAt;

    @Column(nullable = false,updatable = false)
    @Enumerated(EnumType.STRING)
    private Role userRole =  Role.ADMIN;

    @Column(nullable = false)
    private Boolean emailVerified = false;
}
