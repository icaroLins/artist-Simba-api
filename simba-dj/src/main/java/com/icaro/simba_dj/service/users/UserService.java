package com.icaro.simba_dj.service.users;

import com.icaro.simba_dj.dto.request.login.LoginRequest;
import com.icaro.simba_dj.dto.request.token.TokenRequest;
import com.icaro.simba_dj.dto.request.users.UserRequest;
import com.icaro.simba_dj.dto.response.login.LoginResponse;
import com.icaro.simba_dj.dto.response.users.UserResponse;
import com.icaro.simba_dj.exception.EmailAlreadyExistsException;
import com.icaro.simba_dj.exception.ResourceNotFoundException;
import com.icaro.simba_dj.exception.UnauthorizedException;
import com.icaro.simba_dj.mappers.users.UserMapper;
import com.icaro.simba_dj.model.users.User;
import com.icaro.simba_dj.repository.users.UserRepository;
import com.icaro.simba_dj.service.token.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    public UserResponse createUser(UserRequest userRequest) {
        if(userRepository.findByUserEmail(userRequest.userEmail()) != null){
            throw new EmailAlreadyExistsException("This email has already been registered.");
        }

        String passwordEncrypted = passwordEncoder.encode(userRequest.userPassword_hash());

        User newUser = userMapper.toEntity(userRequest);

        newUser.setUserPassword_hash(passwordEncrypted);

        User savedUser = userRepository.save(newUser);

        return userMapper.toResponse(savedUser);
    }

    public LoginResponse loginUser(LoginRequest loginRequest) {
        String token;
        User user = userRepository.findByUserEmail(loginRequest.email());

        if(user == null){
            throw new UnauthorizedException("Invalid email or password.");
        }

        if(!passwordEncoder.matches(loginRequest.password(), user.getUserPassword_hash())){
            throw new UnauthorizedException("Invalid email or password.");
        }

        TokenRequest tokenRequest = new TokenRequest(user.getUserEmail(), user.getUserId(), user.getUserRole());

        token = jwtService.generationToken(tokenRequest);

        return new LoginResponse(token, user.getUserEmail(), user.getUserId(), user.getUserRole());
    }

    public UserResponse searchByEmail(String email){
        User user = userRepository.findByUserEmail(email);

        if(user == null){
            throw new ResourceNotFoundException("User not found.");
        }

        return userMapper.toResponse(user);
    }


}
