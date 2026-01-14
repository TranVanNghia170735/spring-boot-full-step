package com.backend_fullstep.controller.response;

import com.backend_fullstep.common.Gender;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthday;
    private String userName;
    private String email;
    private String phone;
    // more
}
