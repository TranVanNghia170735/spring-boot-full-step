package com.backend_fullstep.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ErrorResponse  {

    private LocalDate timestamp;
    private int status;
    private String path;
    private String error;
    private String message;
    
}
