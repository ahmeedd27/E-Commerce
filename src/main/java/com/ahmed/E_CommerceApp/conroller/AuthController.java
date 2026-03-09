package com.ahmed.E_CommerceApp.conroller;

import com.ahmed.E_CommerceApp.Config.LoginRateLimiter;
import com.ahmed.E_CommerceApp.dao.ResendConfirmationRequest;
import com.ahmed.E_CommerceApp.dto.*;
import com.ahmed.E_CommerceApp.exception.TooManyRequestsException;
import com.ahmed.E_CommerceApp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final LoginRateLimiter loginRateLimiter;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @RequestBody @Valid LoginRequest loginRequest
            , HttpServletRequest request) {
        // Extract the client's IP address
        String clientIp = loginRateLimiter.getClientIp(request);

        // Check if this IP is allowed to attempt login
        if (!loginRateLimiter.isLoginAllowed(clientIp)) {
            throw new TooManyRequestsException(
                    "Too many login attempts. Please wait before trying again."
            );
        }
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody @Valid RegisterRequest request,
            HttpServletRequest httpRequest) {

        String clientIp = loginRateLimiter.getClientIp(httpRequest);
        if (!loginRateLimiter.isRegisterAllowed(clientIp)) {
            throw new TooManyRequestsException(
                    "Too many registration attempts. Please wait before trying again."
            );
        }
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
    public ResponseEntity<String> confirmEmail(
            @RequestBody @Valid EmailConfirmationRequest request,
            HttpServletRequest httpRequest) {

        String clientIp = loginRateLimiter.getClientIp(httpRequest);
        if (!loginRateLimiter.isConfirmEmailAllowed(clientIp)) {
            throw new TooManyRequestsException(
                    "Too many confirmation attempts. Please wait before trying again."
            );
        }
        return ResponseEntity.ok(userService.confirmEmail(request));
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<String> resendConfirmationCode(
            @RequestBody @Valid ResendConfirmationRequest request,
            HttpServletRequest httpRequest) {

        // Rate limit it — same as confirm-email
        // Without this, attacker can spam the email server
        String clientIp = loginRateLimiter.getClientIp(httpRequest);
        if (!loginRateLimiter.isConfirmEmailAllowed(clientIp)) {
            throw new TooManyRequestsException(
                    "Too many requests. Please wait before trying again."
            );
        }

        return ResponseEntity.ok(
                userService.resendConfirmationCode(request.getEmail())
        );
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


    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(userService.refreshAccessToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        return ResponseEntity.ok(userService.logout(authentication));
    }



}
