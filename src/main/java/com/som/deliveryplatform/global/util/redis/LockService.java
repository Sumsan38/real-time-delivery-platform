package com.som.deliveryplatform.global.util.redis;

import java.util.concurrent.Callable;

public interface LockService {
    <T> T executeWithLock(String key, long waitTIle, long leaseTime, Callable<T> task);
}
