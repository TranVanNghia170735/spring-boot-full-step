package com.backend_fullstep.service;

import com.backend_fullstep.model.RedisToken;

public interface RedisTokenService {
    void save(RedisToken redisToken);
    void remove(String id);
    boolean isExists(String id);
}
