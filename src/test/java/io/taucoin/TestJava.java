package io.taucoin;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TestJava {
    Logger logger = LoggerFactory.getLogger("test");
    int counter = 0;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            logger.info("Thread start. Count:{}", counter);
        }
    };

    private void sleepLoop() {
//        while (true) {
            try {
                logger.info("Sleep...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.info("-----interrupt-----");
                logger.info(e.getMessage(), e);
//                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
//        }
    }

    Runnable sleepLoopRunnable = new Runnable() {
        @Override
        public void run() {
            logger.info("------------");
            while (!Thread.currentThread().isInterrupted()) {
                sleepLoop();
            }
        }
    };

    @Test
    public void testInterrupt() {
        Thread thread = new Thread(sleepLoopRunnable, "sleepLoop");
        thread.start();
        try {
            Thread.sleep(5000);
            thread.interrupt();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Test
    public void testScheduledThreadPool() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        ScheduledFuture<?> txSubTimer = null;
        while (true) {
            if (3 == counter) {
                break;
            }
            counter++;
            if (txSubTimer != null) {
                txSubTimer.cancel(true);
            }

            txSubTimer = scheduledExecutorService.schedule(runnable, 500, TimeUnit.MILLISECONDS);
        }
        try {
            Thread.sleep(9000);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
