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

    public Boolean existsHash(final String key, final String field) {
        return hashOperations.hasKey(key, field);
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

    public Double getProductScore(String key, String member) {
        return zSetOperations.score(key, member);
    }
}
