package com.backend_fullstep.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Serializable;

@Getter
@Setter
@Builder
public class ApiResponse implements Serializable {
    private int status;
    private String message;
    private Object data;

    private void writeObject(java.io.ObjectOutputStream stream)throws IOException {
        stream.defaultWriteObject();
    }

    private  void readObject (java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException{
        stream.defaultReadObject();
    }
}
