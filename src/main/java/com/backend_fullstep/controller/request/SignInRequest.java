package com.backend_fullstep.controller.request;

import com.backend_fullstep.utils.Platform;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {

    @NotBlank(message = "username must be not null")
    private String username;

    @NotBlank(message = "username must be not blank")
    private String password;

    @NotBlank(message = "username must be not null")
    private Platform platform;

    private String deviceToken;

    private String version;
}
