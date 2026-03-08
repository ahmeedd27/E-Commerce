package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.Config.CustomUserDetails;
import com.ahmed.E_CommerceApp.dao.UserRepo;
import com.ahmed.E_CommerceApp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found"));
        return new CustomUserDetails(user);
    }
}
