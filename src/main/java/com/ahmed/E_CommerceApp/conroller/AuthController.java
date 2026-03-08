package com.ahmed.E_CommerceApp.conroller;

import com.ahmed.E_CommerceApp.dto.*;
import com.ahmed.E_CommerceApp.model.User;
import com.ahmed.E_CommerceApp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest ,
            Authentication authentication
            ){
               return ResponseEntity.ok(userService.changePassword(authentication,changePasswordRequest));
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(@RequestBody @Valid EmailConfirmationRequest request) {
        return ResponseEntity.ok(userService.confirmEmail(request));
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserSummaryResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/user/role")
    public ResponseEntity<String> getUserRole(Authentication connectedUser){
        return ResponseEntity.ok(userService.getUserRole(connectedUser));
    }



}
