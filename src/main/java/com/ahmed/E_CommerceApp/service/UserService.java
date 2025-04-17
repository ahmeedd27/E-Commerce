package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.dao.UserRepo;
import com.ahmed.E_CommerceApp.dto.ChangePasswordRequest;
import com.ahmed.E_CommerceApp.dto.EmailConfirmationRequest;
import com.ahmed.E_CommerceApp.dto.LoginRequest;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    public ResponseEntity<User> register(User user){
        if(userRepo.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalStateException("this email exist");
        }
        user.setRole(User.Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmationCode(generateConfirmationCode());
        user.setEmailConfirmation(false);
emailService.sendConfirmationCode(user);
        return ResponseEntity.ok(userRepo.save(user));
    }

    public User getUserByEmail(String email){
        return userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    }

    public ResponseEntity<String> changePassword(Authentication connectedUser, ChangePasswordRequest request){
        User user=(User) connectedUser.getPrincipal();

        if(!passwordEncoder.matches(request.getCurrentPassword() , user.getPassword())){ // must current first in comparison because i will enter current first in postman
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

    private String generateConfirmationCode(){
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public ResponseEntity<String> loginUser(
            @RequestBody @Valid LoginRequest request
            ){
        var authUser=authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail() , request.getPassword())
        );
        User user=(User) authUser.getPrincipal();
        Map<String , Object> claims=new HashMap<>();
        claims.put("fullName" , user.getUsername());
        String token=jwtService.generateToken(user);
        return ResponseEntity.ok(token);
    }


    public ResponseEntity<String> confirmEmail(EmailConfirmationRequest request) {
        User user=userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        if(user.getConfirmationCode().equals(request.getConfirmationCode())){
            user.setEmailConfirmation(true);
            user.setConfirmationCode(null);
            userRepo.save(user);
            return ResponseEntity.ok("Confirmed Successfully");
        }else{
            throw new BadCredentialsException("Invalid Code");
        }

    }

    public ResponseEntity<String> getUserRole(Authentication connectedUser) {
        User user=(User) connectedUser.getPrincipal();
        String role=String.valueOf(user.getRole());
        return ResponseEntity.ok(role);
    }
}
