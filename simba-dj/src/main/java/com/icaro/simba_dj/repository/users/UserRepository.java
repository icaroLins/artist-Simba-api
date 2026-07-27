package com.icaro.simba_dj.repository.users;

import com.icaro.simba_dj.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoy extends JpaRepository<User,Integer> {
    User findByUserEmail(String email);
}
