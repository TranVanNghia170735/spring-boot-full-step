package com.backend_fullstep.service.impl;

import com.backend_fullstep.model.Token;
import com.backend_fullstep.repository.TokenRepository;
import com.backend_fullstep.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    @Override
    public Token getByUsername(String username) {
        return null;
    }

    @Override
    public int save(Token token) {
        Optional<Token> optionalToken = tokenRepository.findByUserName(token.getUserName());
        if(optionalToken.isEmpty()){
            tokenRepository.save(token);
            return token.getId();
        } else {
            Token t = optionalToken.get();
            t.setAccessToken(token.getAccessToken());
            t.setRefreshToken(token.getRefreshToken());
            tokenRepository.save(t);
            return t.getId();
        }
    }

    @Override
    public void delete(String username) {

    }
}
