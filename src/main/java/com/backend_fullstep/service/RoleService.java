package com.backend_fullstep.service;

import com.backend_fullstep.model.Role;
import com.backend_fullstep.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    @PostConstruct
    public List<Role> findAll(){
        List<Role> roles = roleRepository.findAllRolesByUserId(26l);
        return roles;
    }

}
