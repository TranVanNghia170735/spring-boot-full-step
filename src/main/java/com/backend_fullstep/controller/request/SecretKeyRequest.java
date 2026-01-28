package com.backend_fullstep.controller.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SecretKeyRequest {
    private String secretKey;
}
