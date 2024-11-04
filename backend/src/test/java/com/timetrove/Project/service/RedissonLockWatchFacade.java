package com.timetrove.Project.service;

import com.timetrove.Project.dto.CartDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

//@Component
public class RedissonLockWatchFacade {
/*
    private final RedissonClient redisson;
    private final CartService cartService;

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";
    private final WatchService watchService;

    public void processPurchase(List<CartDto> cartDtoList) {

        String key = REDISSON_LOCK_PREFIX + cartDtoList.get(0).getWatchId();
        // RedissonClient을 활용하여 Lock 객체 조회
        RLock lock = redisson.getLock(key);

        try {
            // Lock 시도(대기 시간(초), Lock 유효시간, 단위 지정)
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!available) {
                log.info("Redisson Lock 획득 실패");
                return;
            }

            // Lock 획득시 로직 수행
            cartService.processPurchase(cartDtoList);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock 해제 실패");
            }
        }
    }*/
}