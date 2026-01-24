package com.backend_fullstep.service;


import com.backend_fullstep.controller.request.SignInRequest;
import com.backend_fullstep.controller.response.TokenResponse;
import com.backend_fullstep.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public TokenResponse authenticate(SignInRequest signInRequest){
        var user = userRepository.findByUserName(signInRequest.getUsername()).orElseThrow(()-> new UsernameNotFoundException("Username or password incorrect"));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));

        String accessToken = jwtService.generateToken(user);
        return TokenResponse.builder()
                .accessToken("access_token")
                .refreshToken("refresh_token")
                .userId(user.getId())
                .build();
    }
}
