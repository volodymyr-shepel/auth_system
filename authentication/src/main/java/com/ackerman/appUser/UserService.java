package com.ackerman.appUser;

import com.ackerman.util.CustomOAuth2User;
import com.ackerman.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    private final JwtUtil jwtUtil;

    private final AppUserDetailsService appUserDetailsService;

    @Autowired
    public UserService(JwtUtil jwtUtil, AppUserDetailsService appUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.appUserDetailsService = appUserDetailsService;
    }


    public void processCustomOAuthPostLogin(CustomOAuth2User user, HttpServletRequest request, HttpServletResponse response) {
        UserDetails extractedUser = appUserDetailsService.loadUserByUsername(user.getEmail()); // Extracted user from the database
        String jwtToken = jwtUtil.generateToken(extractedUser);
        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure()); // Optionally set the "secure" flag for HTTPS
        cookie.setPath("/"); // Set the cookie path as needed
        response.addCookie(cookie);
    }
}
