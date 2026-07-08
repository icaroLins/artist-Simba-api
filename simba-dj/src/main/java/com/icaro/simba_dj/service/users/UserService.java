package com.icaro.simba_dj.service.users;

import com.icaro.simba_dj.dto.request.users.UserRequest;
import com.icaro.simba_dj.dto.response.users.UserResponse;
import com.icaro.simba_dj.mappers.users.UserMapper;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.repository.users.UserRepositoy;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepositoy userRepositoy;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserRequest userRequest) {
        if(userRepositoy.findByUserEmail(userRequest.userEmail()) != null){
            throw new RuntimeException("This email has already been registered.");
        }

        String passwordEncrypted = passwordEncoder.encode(userRequest.userPasseord_hash());

        User newUser = userMapper.toEntity(userRequest);

        newUser.setUserPasseord_hash(passwordEncrypted);

        User savedUser = userRepositoy.save(newUser);

        UserResponse response = userMapper.toResponse(savedUser);

        return response;
    }
}
