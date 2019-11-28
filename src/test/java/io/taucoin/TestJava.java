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
