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
    public <T> T executeWithLock(String key, long waitTIle, long leaseTime, Callable<T> task) {
        RLock lock = redissonClient.getLock(key);
        boolean isLocked = false;

        try {
            // waitTIle 초 대기하며 락을 얻으려고 시도.
            // 락을 얻은 뒤 waitTIle 초 동안 점유
            isLocked = lock.tryLock(waitTIle, leaseTime, TimeUnit.SECONDS);
            if(! isLocked) {
                throw new IllegalStateException("fail to lock redis key: " + key);
            }
            return task.call();
        } catch (Exception e) {
            log.error("fail to lock redis key: {}", key, e);
            throw new IllegalStateException(e);
        } finally {
            if(isLocked) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.error("fail to unlock redis key: {}", key, e);
                }
            }
        }
    }
}
