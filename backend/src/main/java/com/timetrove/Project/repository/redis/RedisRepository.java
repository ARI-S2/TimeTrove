package com.timetrove.Project.repository.redis;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Object> hashOperations;
    private final ZSetOperations<String, Object> zSetOperations;

    public RedisRepository(final RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    public <T> void saveHash(final String key, final String field, final T value, final Long duration) {
        hashOperations.put(key, field, value);
        redisTemplate.expire(key, duration, TimeUnit.SECONDS);
    }

    public <T> T findHash(final String key, final String field) {
        return (T)hashOperations.get(key, field);
    }

    public void deleteHash(final String key, final String field) {
        hashOperations.delete(key, field);
    }


    public void incrementScore(String key, String id, double score) {
        zSetOperations.incrementScore(key, id, score);
    }

    public Set<Object> getTopRankedItems(String key, int limit) {
        return zSetOperations.reverseRange(key, 0, limit - 1);
    }

    public Double getScore(String key, String id) {
        return zSetOperations.score(key, id);
    }

    /**
     * 결제 요청 멱등성 보장을 위한 키 설정
     * @param key 멱등성 키 (메서드키-사용자ID-금액 등)
     * @param expiration 키 만료 시간 (초)
     * @return 키가 새로 설정되었으면 true, 이미 존재하면 false
     */
    public boolean setIdempotencyKey(String key, long expiration) {
        return redisTemplate.opsForValue().setIfAbsent(key, "success", expiration, TimeUnit.SECONDS);
    }

    public boolean existsIdempotencyKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteIdempotencyKey(String key) {
        redisTemplate.delete(key);
    }
}
