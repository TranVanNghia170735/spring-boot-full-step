package com.backend_fullstep.service.impl;


import com.backend_fullstep.common.TokenType;
import com.backend_fullstep.controller.request.SignInRequest;
import com.backend_fullstep.controller.response.TokenResponse;
import com.backend_fullstep.exception.InvalidDataException;
import com.backend_fullstep.model.Token;
import com.backend_fullstep.repository.UserRepository;
import com.backend_fullstep.service.AuthenticationService;
import com.backend_fullstep.service.JwtService;
import com.backend_fullstep.service.TokenService;
import com.backend_fullstep.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final TokenService tokenService;

    @Override
    public TokenResponse accessToken(SignInRequest signInRequest) {
        log.info("----------- accessToken-------------");

        var user = userService.getByUsername(signInRequest.getUsername());
        if (!user.isEnabled()) {
            throw new InvalidDataException("User not active");
        }
        List<String> roles = userService.getAllRolesByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role)).toList();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword(), authorities));

        //Create new access token
        String accessToken = jwtService.generateToken(user);

        //Create new refresh token
        String refreshToken = jwtService.generateRefreshToken(user);

        String useName = user.getUsername();

        //Save token to db.
        tokenService.save(Token.builder()
                .userName(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();


    }

    @Override
    public TokenResponse refreshToken(HttpServletRequest request) {
        log.info("----------- refreshToken-------------");

        final String refreshToken = request.getHeader(HttpHeaders.REFERER);
        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidDataException("Token must be not blank");
        }
        final String userName = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);
        var user = userService.getByUsername(userName);
        if (!jwtService.isValid(refreshToken, TokenType.REFRESH_TOKEN, user)) {
            throw new InvalidDataException("Not allow access with this token");
        }

        // Create new access token
        String accessToken = jwtService.generateToken(user);

        // Save token to db
        tokenService.save(Token.builder()
                .userName(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken).build());


        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();

    }

    @Override
    public String removeToken(HttpServletRequest request) {
        log.info("----------- Remove Token-------------");

        final String removeToken = request.getHeader(HttpHeaders.REFERER);
        if(StringUtils.isBlank(removeToken)){
            throw new InvalidDataException("Token must be not blank");
        }

        final String userName = jwtService.extractUsername(removeToken, TokenType.REFRESH_TOKEN);
        var user = userService.getByUsername(userName);

        if (!jwtService.isValid(removeToken, TokenType.REFRESH_TOKEN, user)) {
            throw new InvalidDataException("Not allow access with this token");
        }
        tokenService.delete(userName);

        return "Removed";
    }
}
