package com.icaro.simba_dj.controller.users;

import com.icaro.simba_dj.dto.request.login.LoginRequest;
import com.icaro.simba_dj.dto.request.users.UserRequest;
import com.icaro.simba_dj.dto.response.login.LoginResponse;
import com.icaro.simba_dj.dto.response.users.UserResponse;
import com.icaro.simba_dj.service.users.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simba")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserRequest userRequest) {
        UserResponse userResponse = userService.createUser(userRequest);

        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.loginUser(loginRequest);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<?> findUser(Authentication authentication) {
        String email = authentication.getName();

        UserResponse userResponse = userService.searchByEmail(email);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
}
