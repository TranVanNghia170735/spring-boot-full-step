package com.backend_fullstep.service;

import com.backend_fullstep.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceDetail  {
    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
