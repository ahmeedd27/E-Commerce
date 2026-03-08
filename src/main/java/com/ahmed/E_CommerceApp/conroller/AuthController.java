package com.ahmed.E_CommerceApp.conroller;

import com.ahmed.E_CommerceApp.dto.*;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.model.User;
import com.ahmed.E_CommerceApp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest ,
            Authentication authentication
            ){
               return userService.changePassword(authentication,changePasswordRequest);
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(@RequestBody EmailConfirmationRequest request) {
        return userService.confirmEmail(request);
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')") // only admins
    public ResponseEntity<String> getUserEmailById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id).getEmail());
    }

    @GetMapping("/user/role")
    public ResponseEntity<String> getUserRole(Authentication connectedUser){
        return userService.getUserRole(connectedUser);
    }



}
