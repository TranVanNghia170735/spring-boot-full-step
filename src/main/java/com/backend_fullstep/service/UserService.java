package com.backend_fullstep.service;

import com.backend_fullstep.controller.request.UserCreationRequest;
import com.backend_fullstep.controller.request.UserPasswordRequest;
import com.backend_fullstep.controller.request.UserUpdateRequest;
import com.backend_fullstep.controller.response.UserPageResponse;
import com.backend_fullstep.controller.response.UserResponse;
import com.backend_fullstep.model.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    UserPageResponse findAll(String keyword, String sort, int page, int size);
    UserResponse findById(Long id);
    UserResponse findByUserName (String name);
    UserResponse findByEmail(String email);
    long save (UserCreationRequest req);
    void update (UserUpdateRequest req);
    void changePassword(UserPasswordRequest req);
    void delete (Long id);

    UserDetailsService userDetailsService();
    UserEntity getByUsername(String userName);
    List<String> getAllRolesByUserId(long userId);
    UserEntity getUserByEmail(String email);
}
