package com.icaro.simba_dj.model.users;

import com.icaro.simba_dj.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_tb")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userId;

    @Column(unique = true)
    String userEmail;

    String userPasseord_hash;

    @CreationTimestamp
            @Column(nullable = false,updatable = false)
    LocalDateTime userCreatedAt;

    @Column(nullable = false,updatable = false)
    Role userRole =  Role.ADMIN;
}
