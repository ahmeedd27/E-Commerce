package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.dao.UserRepo;
import com.ahmed.E_CommerceApp.dto.ChangePasswordRequest;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public User register(User user){
        if(userRepo.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalStateException("this email exist");
        }
        user.setRole(User.Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmationCode(generateConfirmationCode());
        user.setEmailConfirmation(false);

        return userRepo.save(user);
    }

    public User getUserByEmail(String email){
        return userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    }

    public void changePassword(String email , ChangePasswordRequest request){
        User user=userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(!passwordEncoder.matches(user.getPassword(),request.getCurrentPassword())){
            throw new BadCredentialsException("wrong password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
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

}
