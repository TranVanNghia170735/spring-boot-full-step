package com.backend_fullstep.exception;

public class ResourceNotFoundException extends  RuntimeException{
    public ResourceNotFoundException (String message){
        super(message);

    }
}
