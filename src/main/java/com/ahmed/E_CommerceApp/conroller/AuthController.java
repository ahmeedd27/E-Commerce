package com.ahmed.E_CommerceApp.conroller;

import com.ahmed.E_CommerceApp.dto.ChangePasswordRequest;
import com.ahmed.E_CommerceApp.dto.EmailConfirmationRequest;
import com.ahmed.E_CommerceApp.dto.LoginRequest;
import com.ahmed.E_CommerceApp.dto.RegisterResponse;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.model.User;
import com.ahmed.E_CommerceApp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(
            @RequestBody @Valid LoginRequest loginRequest
            ){
        return userService.loginUser(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid User user) { //  add @Valid
        return userService.register(user);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest ,
            Authentication authentication
            ){
               return userService.changePassword(authentication,changePasswordRequest);
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(@RequestBody EmailConfirmationRequest request) {
        return userService.confirmEmail(request);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<String> getUserEmailById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id).getEmail());
    }

    @GetMapping("/user/role")
    public ResponseEntity<String> getUserRole(Authentication connectedUser){
        return userService.getUserRole(connectedUser);
    }



}
