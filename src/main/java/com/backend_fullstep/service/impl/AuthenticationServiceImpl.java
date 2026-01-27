package com.backend_fullstep.service.impl;


import com.backend_fullstep.controller.request.RefreshTokenRequest;
import com.backend_fullstep.controller.request.SignInRequest;
import com.backend_fullstep.controller.response.TokenResponse;
import com.backend_fullstep.exception.InvalidDataException;
import com.backend_fullstep.model.Token;
import com.backend_fullstep.repository.UserRepository;
import com.backend_fullstep.service.AuthenticationService;
import com.backend_fullstep.service.JwtService;
import com.backend_fullstep.service.TokenService;
import com.backend_fullstep.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        if(!user.isEnabled()){
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
    public TokenResponse refreshToken(RefreshTokenRequest refreshToken) {
        log.info("Get refresh token");
        return null;

    }
}
