package com.backend_fullstep.service.impl;


import com.backend_fullstep.common.TokenType;
import com.backend_fullstep.controller.request.ForgotPasswordRequest;
import com.backend_fullstep.controller.request.ResetPasswordDTO;
import com.backend_fullstep.controller.request.SecretKeyRequest;
import com.backend_fullstep.controller.request.SignInRequest;
import com.backend_fullstep.controller.response.TokenResponse;
import com.backend_fullstep.exception.InvalidDataException;
import com.backend_fullstep.model.Token;
import com.backend_fullstep.model.UserEntity;
import com.backend_fullstep.repository.UserRepository;
import com.backend_fullstep.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final RedisTokenService redisTokenService;

    @Override
    public TokenResponse accessToken(SignInRequest signInRequest) {
        log.info("----------- accessToken-------------");

        var user = userService.getByUsername(signInRequest.getUsername());
        if (!user.isEnabled()) {
            throw new InvalidDataException("User not active");
        }
        List<String> roles = userService.getAllRolesByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role)).toList();


        try {
            Authentication authentication= authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword(), authorities));

            log.info("isAuthenticated = {}", authentication.getAuthorities().toString());
            // SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            throw new AccessDeniedException(e.getMessage());
        }
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

        // Save token to redis
//        redisTokenService.save(RedisToken.builder()
//                .id(user.getUsername())
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build());

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

        // Save token to redis
//        redisTokenService.save(RedisToken.builder()
//                .id(user.getUsername())
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build());

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
//        redisTokenService.remove(userName);


        return "Removed";
    }

    @Override
    public String forgotPassword(ForgotPasswordRequest email) {
        log.info("----------- Forgot Password-------------");

        //Check email exists or not
        UserEntity userEntity = userService.getUserByEmail(email.getEmail());

        // Generate reset token.
        String resetToken = jwtService.generateResetToken(userEntity);

        // Save to db
        tokenService.save(Token.builder()
                        .userName(userEntity.getUsername())
                        .resetToken(resetToken)
                        .build());

        // Save to redis
//        redisTokenService.save(RedisToken.builder()
//                .id(userEntity.getUsername())
//                .resetToken(resetToken)
//                .build());


        // TODO send email to user
        String confirmLink = String.format("curl --location 'http://localhost:80/auth/reset-password' \\\n" +
                "--header 'accept: */*' \\\n" +
                "--header 'Content-Type: application/json' \\\n" +
                "--data '%s'", resetToken);
        return resetToken;
    }

    @Override
    public String resetPassword(SecretKeyRequest secretKey) {
        log.info("----------- Reset Password-------------");

        // validate token
        var user = validateToken(secretKey.getSecretKey());

        // check token by username
        tokenService.getByUsername(user.getUsername());

        return "Reset";
    }

    @Override
    public String changePassword(ResetPasswordDTO request) {
        log.info("----------- Change Password-------------");
        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new InvalidDataException("Password do not match");
        }

        //get user by reset token
        var user = validateToken(request.getSecretKey());

        //update password
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.saveUser(user);
        return "Changed";
    }

    private UserEntity validateToken(String token){

        // validate token
        var userName = jwtService.extractUsername(token, TokenType.RESET_TOKEN);

        // Check token in redis
//        redisTokenService.isExists(userName);

        // validate user is active or not
        var user = userService.getByUsername(userName);
        if(!user.isEnabled()){
            throw new InvalidDataException("User not active");
        }
        return user;
    }
}
