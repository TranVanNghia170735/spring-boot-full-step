package com.backend_fullstep.controller;


import com.backend_fullstep.controller.request.RefreshTokenRequest;
import com.backend_fullstep.controller.request.SignInRequest;
import com.backend_fullstep.controller.response.TokenResponse;
import com.backend_fullstep.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
@Validated
@Tag(name="Authentication Controller")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Access token", description = "Get access token and refresh token by username and password")
    @PostMapping("/access-token")
    public TokenResponse accessToken(@RequestBody SignInRequest request){
        log.info("Access token request");
        return authenticationService.getAccessToken(request);
    }

    @Operation(summary = "Refresh token", description = "Get access token by refresh token")
    @PostMapping("/refresh-token")
    public TokenResponse refreshToken (@RequestBody RefreshTokenRequest refreshToken){
        log.info("Refresh token request");
        return authenticationService.getRefreshToken(refreshToken);
    }
}
