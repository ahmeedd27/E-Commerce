package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.Config.CustomUserDetails;
import com.ahmed.E_CommerceApp.dao.UserRepo;
import com.ahmed.E_CommerceApp.dto.*;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.model.RefreshToken;
import com.ahmed.E_CommerceApp.model.User;
import com.ahmed.E_CommerceApp.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;


    public RegisterResponse register(RegisterRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already exists");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .confirmationCode(generateConfirmationCode())
                .confirmationCodeExpiresAt(LocalDateTime.now().plusMinutes(15))
                .emailConfirmation(false)
                .build();
        emailService.sendConfirmationCode(user);
        User saved = userRepo.save(user);
        return new RegisterResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getRole().name(),
                "Registration successful. Please check your email to confirm."
        );
    }



    public String changePassword(Authentication connectedUser, ChangePasswordRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        User user = userDetails.getUser();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("wrong password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        return "changed successfully";
    }



    private String generateConfirmationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public LoginResponse loginUser(LoginRequest request) {
        var authUser = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authUser.getPrincipal();

        // Generate short-lived access token
        String accessToken = jwtService.generateToken(userDetails);

        // Generate long-lived refresh token and save to DB
        RefreshToken refreshToken = refreshTokenService
                .createRefreshToken(userDetails.getUser());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .type("Bearer")
                .email(userDetails.getUsername())
                .role(userDetails.getUser().getRole().name())
                .build();
    }

    public RefreshTokenResponse refreshAccessToken(RefreshTokenRequest request) {
        // Validate refresh token — throws if invalid or expired
        RefreshToken refreshToken = refreshTokenService
                .validateRefreshToken(request.getRefreshToken());

        // Get the user from the refresh token
        User user = refreshToken.getUser();

        // Wrap in CustomUserDetails to generate new access token
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtService.generateToken(userDetails);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .type("Bearer")
                .build();
    }

    public String logout(Authentication connectedUser) {
        CustomUserDetails userDetails =
                (CustomUserDetails) connectedUser.getPrincipal();
        // Delete refresh token from DB — token is now dead
        refreshTokenService.deleteByUser(userDetails.getUser());
        return "Logged out successfully";
    }


    public String confirmEmail(EmailConfirmationRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check 1 — already confirmed?
        if (user.getConfirmationCode() == null) {
            throw new BadCredentialsException("Email already confirmed");
        }

        // Check 2 — is the code expired?  NEW
        if (user.getConfirmationCodeExpiresAt() == null ||
                LocalDateTime.now().isAfter(user.getConfirmationCodeExpiresAt())) {
            throw new BadCredentialsException(
                    "Confirmation code has expired. Please request a new one."
            );
        }

        // Check 3 — is the code correct?
        if (!user.getConfirmationCode().equals(request.getConfirmationCode())) {
            throw new BadCredentialsException("Invalid confirmation code");
        }

        // All checks passed — confirm the email
        user.setEmailConfirmation(true);
        user.setConfirmationCode(null);
        user.setConfirmationCodeExpiresAt(null); //  clean up expiry too
        userRepo.save(user);
        return "Confirmed Successfully";
    }

    // this method meant for one who register and want to confirm after he register by more than 15 minutes
    public String resendConfirmationCode(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // No point resending if already confirmed
        if (user.isEmailConfirmation()) {
            throw new IllegalStateException("Email is already confirmed");
        }

        // Generate a fresh code with a fresh 15-minute window
        user.setConfirmationCode(generateConfirmationCode());
        user.setConfirmationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        userRepo.save(user);

        emailService.sendConfirmationCode(user);
        return "New confirmation code sent. Please check your email.";
    }


    public UserSummaryResponse getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return  UserSummaryResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .build();
     }

    public String getUserRole(Authentication connectedUser) {
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        return String.valueOf(userDetails.getUser().getRole());
    }
}
