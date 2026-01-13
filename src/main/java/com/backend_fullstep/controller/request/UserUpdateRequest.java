package com.backend_fullstep.controller.request;

import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
public class UserUpdateRequest implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private Date birthday;
    private String username;
    private String email;
    private String phone;
}
