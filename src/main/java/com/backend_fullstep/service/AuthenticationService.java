package com.backend_fullstep.service;

import com.backend_fullstep.controller.request.RefreshTokenRequest;
import com.backend_fullstep.controller.request.SignInRequest;
import com.backend_fullstep.controller.response.TokenResponse;


public interface AuthenticationService {
    TokenResponse accessToken(SignInRequest request);
    TokenResponse refreshToken(RefreshTokenRequest request);
}
