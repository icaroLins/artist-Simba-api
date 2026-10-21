package com.icaro.simba_dj.repository.users;

import com.icaro.simba_dj.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUserEmail(String email);
}
