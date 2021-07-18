package org.vari.clockify.webhook.accessdecision;

import com.google.cloud.functions.HttpRequest;
import com.google.common.util.concurrent.RateLimiter;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RateLimitFilter implements AccessDecisionFilter {
    RateLimiter rateLimiter = RateLimiter.create(120);

    @Override
    public AccessDecision vote(HttpRequest request) {
        return !rateLimiter.tryAcquire() //
                ? AccessDecision.ok()
                : AccessDecision.failed("Too Many Requests", -1, "Too Many Requests");
    }

}
