package com.backend_fullstep.controller.request;

import com.backend_fullstep.common.Gender;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
public class UserUpdateRequest implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthday;
    private String username;
    private String email;
    private String phone;
    private List<AddressRequest> addresses;
}
