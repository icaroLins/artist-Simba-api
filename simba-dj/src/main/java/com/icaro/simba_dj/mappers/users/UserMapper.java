package com.icaro.simba_dj.mappers.users;

import com.icaro.simba_dj.dto.request.users.UserRequest;
import com.icaro.simba_dj.dto.response.users.UserResponse;
import com.icaro.simba_dj.model.users.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRequest userRequest) {
        User user = new User();
        user.setUserEmail(userRequest.userEmail());
        user.setUserPasseord_hash(userRequest.userPasseord_hash());

        return user;
    }

    public UserResponse toResponse(User  user) {
        return  new UserResponse(
              user.getUserEmail(),
                user.getUserId(),
                user.getUserRole()
        );
    }
}
