package com.backend_fullstep.controller.request;

import lombok.Getter;

import java.util.Date;

@Getter
public class UserUpdateRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private Date birthday;
    private String username;
    private String email;
    private String phone;
}
