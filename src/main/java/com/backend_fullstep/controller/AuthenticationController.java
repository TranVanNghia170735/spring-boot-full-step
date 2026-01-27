package com.backend_fullstep.controller;


import com.backend_fullstep.controller.request.RefreshTokenRequest;
import com.backend_fullstep.controller.request.SignInRequest;
import com.backend_fullstep.controller.response.TokenResponse;
import com.backend_fullstep.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/access-token")
    public ResponseEntity<TokenResponse> accessToken(@RequestBody SignInRequest request){
        log.info("Access token request");
        return new ResponseEntity<>(authenticationService.accessToken(request), HttpStatus.OK) ;
    }

    @PostMapping("/refresh-token")
    public TokenResponse refreshToken (@RequestBody RefreshTokenRequest refreshToken){
        log.info("Refresh token request");
        return authenticationService.refreshToken(refreshToken);
    }
}
