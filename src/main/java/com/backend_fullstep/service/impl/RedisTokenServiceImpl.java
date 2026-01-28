package com.backend_fullstep.service.impl;

import com.backend_fullstep.exception.InvalidDataException;
import com.backend_fullstep.model.RedisToken;
import com.backend_fullstep.repository.RedisTokenRepository;
import com.backend_fullstep.service.RedisTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenServiceImpl implements RedisTokenService {
    private final RedisTokenRepository redisTokenRepository;

    @Override
    public void save(RedisToken redisToken) {
        redisTokenRepository.save(redisToken);
    }

    @Override
    public void remove(String id) {
        isExists(id);
        redisTokenRepository.deleteById(id);
    }

    @Override
    public boolean isExists(String id) {
        if(!redisTokenRepository.existsById(id)){
            throw new InvalidDataException("Token not exists");
        }
        return true;
    }
}
