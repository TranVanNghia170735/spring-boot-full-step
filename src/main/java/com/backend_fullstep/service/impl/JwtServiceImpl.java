package com.backend_fullstep.service.impl;

import com.backend_fullstep.common.TokenType;
import com.backend_fullstep.exception.InvalidDataException;
import com.backend_fullstep.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j(topic="JWT-SERVICE")
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiryMinutes}")
    private long expiryMinutes;

    @Value("${jwt.expiryDay}")
    private long expiryDay;

    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;



    @Override
    public String generateAccessToken(String username, List<String> authorities) {
        log.info("Generate access token for username {} with authorities {}", username, authorities);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", authorities);
        return generateToken(claims, username);
    }

    private String generateToken(Map<String, Object> claims, String username){
        log.info("--------------------[ generateToken ]-----------------------");
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expiryMinutes))
                .signWith(getKey(TokenType.ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(String username, List<String> authorities) {
        log.info("Generate refresh token");

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", authorities);
        return generateForRefreshToken(claims, username);
    }

    private String generateForRefreshToken(Map<String, Object> claims, String username) {
        log.info(" Generate for refresh token");
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDay))
                .signWith(getKey(TokenType.REFRESH_TOKEN), SignatureAlgorithm.HS256).compact();
    }

    @Override
    public String extractUsername(String token, TokenType type) {
        return extractClaim(token, type, Claims::getSubject);
    }

    @Override
    public boolean isValid(String token, TokenType type, UserDetails userDetails) {
        final String username = extractUsername(token, type);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, type));
    }


    private Key getKey(TokenType type){
        log.info("------------------ get Key ---------------------");
        switch (type){
            case ACCESS_TOKEN -> {return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
            }
            case REFRESH_TOKEN -> {return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));}
            default -> throw  new InvalidDataException("Invalid token type");
        }
    }

    private <T> T extractClaim(String token, TokenType type, Function<Claims, T> claimResolver){
        log.info("------------------- extractClaim ---------------------------");
        final  Claims claims =  extraAllClaim(token, type);
        return claimResolver.apply(claims);
    }

    private Claims extraAllClaim(String token, TokenType type){
        log.info("---------------------- extraAllClaim -----------------------");
        try {
            return  Jwts.parserBuilder()
                    .setSigningKey(getKey(type))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (SignatureException | ExpiredJwtException e){
           throw new AccessDeniedException("Access denied: " +e.getMessage());
        }
    }

    private boolean isTokenExpired(String token, TokenType type){
        return extractExpiration(token, type).before(new Date());
    }

    private Date extractExpiration(String token, TokenType type){
        return extractClaim(token, type, Claims::getExpiration);
    }
}
