package com.backend_fullstep.controller.request;

import com.backend_fullstep.common.Gender;
import com.backend_fullstep.common.UserType;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
public class UserCreationRequest implements Serializable {
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthday;
    private String username;
    private String email;
    private String phone;
    private UserType type;
    private List<AddressRequest> addresses; //home, office
}
