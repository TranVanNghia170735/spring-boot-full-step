package com.backend_fullstep.service.impl;

import com.backend_fullstep.exception.ResourceNotFoundException;
import com.backend_fullstep.model.Token;
import com.backend_fullstep.repository.TokenRepository;
import com.backend_fullstep.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    @Override
    public Token getByUsername(String username) {
        return tokenRepository.findByUserName(username).orElseThrow(() -> new ResourceNotFoundException("Not found token"));
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
        Token token = getByUsername(username);
        tokenRepository.delete(token);
    }
}
