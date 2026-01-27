package com.backend_fullstep.service;


import com.backend_fullstep.model.Token;

public interface TokenService {

    Token getByUsername(String username);
    int save(Token token);
    void delete(String username);

}
