package com.backend_fullstep.service.impl;

import com.backend_fullstep.service.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {
    @Override
    public String generateToken(UserDetails user) {
        // TODO xu ly tao ra token
        return "access-token";
    }
}
