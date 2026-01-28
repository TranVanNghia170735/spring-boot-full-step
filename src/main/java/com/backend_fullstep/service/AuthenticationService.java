package com.backend_fullstep.service;

import com.backend_fullstep.controller.request.ForgotPasswordRequest;
import com.backend_fullstep.controller.request.ResetPasswordDTO;
import com.backend_fullstep.controller.request.SecretKeyRequest;
import com.backend_fullstep.controller.request.SignInRequest;
import com.backend_fullstep.controller.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;


public interface AuthenticationService {
    TokenResponse accessToken(SignInRequest request);
    TokenResponse refreshToken(HttpServletRequest request);
    String removeToken(HttpServletRequest request);
    String forgotPassword(ForgotPasswordRequest email);
    String resetPassword(SecretKeyRequest secretKey);
    String changePassword(ResetPasswordDTO request);
}
