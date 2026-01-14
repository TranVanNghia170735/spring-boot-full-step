package com.backend_fullstep.controller.request;

import com.backend_fullstep.common.Gender;
import com.backend_fullstep.common.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
public class UserCreationRequest implements Serializable {

    @NotBlank(message = "fistName must be not blank")
    private String firstName;

    @NotBlank(message = "lasName must be not blank")
    private String lastName;
    private Gender gender;
    private LocalDate birthday;
    private String username;

    @Email(message ="Email invalid")
    private String email;
    private String phone;
    private UserType type;
    private List<AddressRequest> addresses; //home, office
}
