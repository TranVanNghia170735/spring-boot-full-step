package com.backend_fullstep.controller.request;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class RefreshTokenRequest implements Serializable {
    private String refreshToken;
}
