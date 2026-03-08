package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.Config.CustomUserDetails;
import com.ahmed.E_CommerceApp.dao.UserRepo;
import com.ahmed.E_CommerceApp.dto.ChangePasswordRequest;
import com.ahmed.E_CommerceApp.dto.EmailConfirmationRequest;
import com.ahmed.E_CommerceApp.dto.LoginRequest;
import com.ahmed.E_CommerceApp.dto.RegisterResponse;
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


    public ResponseEntity<RegisterResponse> register(User user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already exists");
        }
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmationCode(generateConfirmationCode());
        user.setEmailConfirmation(false);
        emailService.sendConfirmationCode(user);
        User saved = userRepo.save(user);
        return ResponseEntity.ok(new RegisterResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getRole().name(),
                "Registration successful. Please check your email to confirm."
        ));
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    }

    public ResponseEntity<String> changePassword(Authentication connectedUser, ChangePasswordRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        User user = userDetails.getUser();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) { // must current first in comparison because i will enter current first in postman
            throw new BadCredentialsException("wrong password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        return ResponseEntity.ok("changed successfully");
    }

    public User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private String generateConfirmationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public ResponseEntity<String> loginUser(
            LoginRequest request
    ) {
        var authUser = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserDetails userDetails = (UserDetails) authUser.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(token);
    }


    public ResponseEntity<String> confirmEmail(EmailConfirmationRequest request) {
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
        return ResponseEntity.ok("Confirmed Successfully");
    }

    public ResponseEntity<String> getUserRole(Authentication connectedUser) {
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        String role = String.valueOf(userDetails.getUser().getRole());
        return ResponseEntity.ok(role);
    }
}
