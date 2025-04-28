package com.som.deliveryplatform.global.util.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLockService implements LockService {

    private final RedissonClient redissonClient;

    @Override
    public <T> T executeWithLock(String key, long waitTime, long leaseTime, Callable<T> task) {
        // waitTime 초 대기하며 락을 얻으려고 시도.
        // 락을 얻은 뒤 waitTime 초 동안 점유
        RLock lock = redissonClient.getLock(key);
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (!locked) {
                throw new IllegalStateException("Redis lock 획득 실패: key=" + key);
            }
            return task.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }
}
