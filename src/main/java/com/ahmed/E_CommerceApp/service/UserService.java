package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.Config.CustomUserDetails;
import com.ahmed.E_CommerceApp.dao.UserRepo;
import com.ahmed.E_CommerceApp.dto.*;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.model.User;
import com.ahmed.E_CommerceApp.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;


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
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        CustomUserDetails userDetails = (CustomUserDetails) authUser.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .email(userDetails.getUsername())
                .role(userDetails.getUser().getRole().name())
                .build();
    }


    public String confirmEmail(EmailConfirmationRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        if (user.getConfirmationCode() == null) {
            throw new BadCredentialsException("Email already confirmed or code expired");
        }
        if (!user.getConfirmationCode().equals(request.getConfirmationCode())) {
            throw new BadCredentialsException("Invalid confirmation code");
        }
        user.setEmailConfirmation(true);
        user.setConfirmationCode(null);
        userRepo.save(user);
        return "Confirmed Successfully";
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
