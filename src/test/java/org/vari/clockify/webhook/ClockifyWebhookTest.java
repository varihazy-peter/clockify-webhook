package org.vari.clockify.webhook;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import com.google.common.util.concurrent.RateLimiter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ClockifyWebhookTest {

    @Test
    void test() throws IOException, InterruptedException, ExecutionException {
        RateLimiter rateLimiter = RateLimiter.create(120);
        for (int i = 0; i < 30; i++) {
            boolean acqed = rateLimiter.tryAcquire(1);
            log.info("acqed: {}: {}", i, acqed);
            Thread.sleep(1);
        }
    }

}
