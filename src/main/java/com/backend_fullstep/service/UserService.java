package com.backend_fullstep.service;

import com.backend_fullstep.controller.request.UserCreationRequest;
import com.backend_fullstep.controller.request.UserPasswordRequest;
import com.backend_fullstep.controller.request.UserUpdateRequest;
import com.backend_fullstep.controller.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    UserResponse findByUserName (String name);
    UserResponse findByEmail(String email);
    long save (UserCreationRequest req);
    void update (UserUpdateRequest req);
    void changePassword(UserPasswordRequest req);
    void delete (Long id);

}
