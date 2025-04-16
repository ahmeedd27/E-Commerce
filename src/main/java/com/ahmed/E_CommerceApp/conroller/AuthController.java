package com.ahmed.E_CommerceApp.conroller;

import com.ahmed.E_CommerceApp.dto.ChangePasswordRequest;
import com.ahmed.E_CommerceApp.dto.LoginRequest;
import com.ahmed.E_CommerceApp.model.User;
import com.ahmed.E_CommerceApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(
            @RequestBody LoginRequest loginRequest
            ){
        return userService.loginUser(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody User user
            ){
          return userService.register(user);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest ,
            Authentication authentication
            ){
               return userService.changePassword(authentication,changePasswordRequest);
    }



}
