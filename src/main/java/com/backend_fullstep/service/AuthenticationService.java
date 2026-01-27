package com.backend_fullstep.service;

import com.backend_fullstep.controller.request.SignInRequest;
import com.backend_fullstep.controller.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;


public interface AuthenticationService {
    TokenResponse accessToken(SignInRequest request);
    TokenResponse refreshToken(HttpServletRequest request);
    String removeToken(HttpServletRequest request);
}
