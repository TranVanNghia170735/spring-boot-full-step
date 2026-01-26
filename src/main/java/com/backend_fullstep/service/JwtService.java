package com.backend_fullstep.service;

import com.backend_fullstep.common.TokenType;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface JwtService {
    String generateAccessToken(String username, List<String> authorities);
    String generateRefreshToken(String username, List<String> authorities);
    String extractUsername(String token, TokenType type);
    boolean isValid (String token, TokenType type, UserDetails user);
}
