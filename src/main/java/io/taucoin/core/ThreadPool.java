package io.taucoin.core;

import io.taucoin.db.RedisDb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.util.concurrent.*;

public class ThreadPool {
    private static final Logger logger = LoggerFactory.getLogger("threadpool");
    private RedisDb redisDb;
//    private ExecutorService pool = Executors.newSingleThreadExecutor();
    private ExecutorService pool = Executors.newFixedThreadPool(2);
//    private ThreadPoolExecutor poo = new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS,
//            new ArrayBlockingQueue<Runnable>(1));

    public ThreadPool(RedisDb redisDb) {
        this.redisDb = redisDb;
    }

    public void start() {
        logger.info("---------------------------Start--------------------------------");
        Thread thread1 = new VerifyThread();
//        Thread thread2 = new VerifyTotalBalanceThread();
        pool.execute(thread1);
//        pool.execute(thread2);

        pool.shutdown();

    }

    class VerifyThread extends Thread {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    Transaction tx = redisDb.getBlockingNewTransaction();
                    if (tx != null && TransactionValidation.accept(redisDb, tx)) {
                        redisDb.tryToPutIntoPool(tx);
                    }
                } catch (Exception e) {
                    logger.error("Verification fail!!!");
                    logger.error(e.getMessage(), e);
                    try {
                        sleep(5000);
                    }
                    catch (Exception s) {
                        logger.error(s.getMessage(), s);
                    }
                }
            }
        }
    }

    class VerifyTotalBalanceThread extends Thread {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    BalanceValidation.VerifyBalance(redisDb);
                } catch (Exception e) {
                    logger.error("BalanceValidation Exception.");
                    logger.error(e.getMessage(), e);
                }

                try {
                    sleep(10000);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

//    class MyTask implements Runnable {
//        private Transaction tx;
//
//        public MyTask(Transaction transaction) {
//            this.tx = transaction;
//        }
//
//        @Override
//        public void run() {
//            while (!Thread.interrupted()) {
//                if (tx != null && TransactionValidation.accept(redisDb, tx)) {
//                    redisDb.tryToPutIntoPool(tx);
//                }
//            }
//        }
//    }

}
