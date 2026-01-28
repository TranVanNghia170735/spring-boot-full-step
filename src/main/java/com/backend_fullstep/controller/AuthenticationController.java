package com.backend_fullstep.controller;


import com.backend_fullstep.controller.request.ForgotPasswordRequest;
import com.backend_fullstep.controller.request.ResetPasswordDTO;
import com.backend_fullstep.controller.request.SecretKeyRequest;
import com.backend_fullstep.controller.request.SignInRequest;
import com.backend_fullstep.controller.response.TokenResponse;
import com.backend_fullstep.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<TokenResponse> refreshToken (HttpServletRequest request){
        log.info("Refresh token request");
        return new ResponseEntity<>(authenticationService.refreshToken(request), HttpStatus.OK);
    }

    @PostMapping("/remove-token")
    public ResponseEntity<String> removeToken (HttpServletRequest request){
        log.info("Remove token request");
        return new ResponseEntity<>(authenticationService.removeToken(request), HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword (@RequestBody ForgotPasswordRequest email){
        log.info("Forgot password request");
        return new ResponseEntity<>(authenticationService.forgotPassword(email), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword (@RequestBody SecretKeyRequest secretKeyRequest){
        log.info("Reset password request");
        return new ResponseEntity<>(authenticationService.resetPassword(secretKeyRequest), HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword (@RequestBody ResetPasswordDTO resetPasswordDTO){
        log.info("Change password request");
        return new ResponseEntity<>(authenticationService.changePassword(resetPasswordDTO), HttpStatus.OK);
    }




}
