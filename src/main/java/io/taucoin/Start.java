package io.taucoin;


import io.taucoin.core.ThreadPool;
import io.taucoin.db.RedisDb;
import io.taucoin.db.RedisDbImpl;
import io.taucoin.util.AdvancedDeviceUtils;


public class Start {
    public static void main(String[] args) {
        AdvancedDeviceUtils.configureDetailedTracing();

        RedisDb redisDb = new RedisDbImpl();
        redisDb.init();

        ThreadPool threadPool = new ThreadPool(redisDb);
        threadPool.start();

    }
}
