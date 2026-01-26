package com.backend_fullstep.service.impl;


import com.backend_fullstep.common.TokenType;
import com.backend_fullstep.controller.request.RefreshTokenRequest;
import com.backend_fullstep.controller.request.SignInRequest;
import com.backend_fullstep.controller.response.TokenResponse;
import com.backend_fullstep.exception.ForBiddenException;
import com.backend_fullstep.exception.InvalidDataException;
import com.backend_fullstep.model.UserEntity;
import com.backend_fullstep.repository.UserRepository;
import com.backend_fullstep.service.AuthenticationService;
import com.backend_fullstep.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("Get access token");
        List<String> authorities = new ArrayList<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            authorities.add(authentication.getAuthorities().toString());

            //Nếu xác thực thành công, lưu thông tin vào SecurityContext.
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (BadCredentialsException  | DisabledException e) {
            log.info("errorMessage: {}", e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }

        String accessToken = jwtService.generateAccessToken(request.getUsername(), authorities);
        String refreshToken = jwtService.generateRefreshToken(request.getUsername(), authorities);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).
                build();
    }

    @Override
    public TokenResponse getRefreshToken(RefreshTokenRequest refreshToken) {
        log.info("Get refresh token");

        if(!StringUtils.hasLength(refreshToken.getRefreshToken())){
            throw new InvalidDataException("Token must be not blank");
        }
        try {
            // Verity token
            String username = jwtService.extractUsername(refreshToken.getRefreshToken(), TokenType.REFRESH_TOKEN);

            // Check user is active or inactivated.
            Optional<UserEntity> user = userRepository.findByUserName(username);

            List<String> authorities = new ArrayList<>();
            user.get().getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));

            // generate new access token.
            String accessToken =jwtService.generateAccessToken(user.get().getUsername(), authorities);
            return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken.getRefreshToken()).build();

        } catch (Exception e){
            log.error("Access denied! errorMessage: {}", e.getMessage());
            throw new ForBiddenException(e.getMessage());
        }

    }
}
