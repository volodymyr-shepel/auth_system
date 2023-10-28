package com.ackerman.appUser;

import com.ackerman.util.CustomOAuth2User;
import com.ackerman.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {


    private final JwtUtil jwtUtil;

    private final AppUserDetailsService appUserDetailsService;

    @Autowired
    public UserService(JwtUtil jwtUtil, AppUserDetailsService appUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.appUserDetailsService = appUserDetailsService;
    }


    public void processCustomOAuthPostLogin(CustomOAuth2User user) {
        UserDetails extractedUser = appUserDetailsService.loadUserByUsername(user.getEmail()); // Extracted user from the database
        String token = jwtUtil.generateToken(extractedUser);
        //TODO:add to cookie or return instead of printing
        System.out.println(token);


    }
}
