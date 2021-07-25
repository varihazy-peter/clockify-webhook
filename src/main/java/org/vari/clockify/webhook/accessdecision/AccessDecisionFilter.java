package org.vari.clockify.webhook.accessdecision;

import com.google.cloud.functions.HttpRequest;

public interface AccessDecisionFilter {
    default boolean supports(HttpRequest request) {
        return true;
    }

    AccessDecision vote(HttpRequest request);
}
