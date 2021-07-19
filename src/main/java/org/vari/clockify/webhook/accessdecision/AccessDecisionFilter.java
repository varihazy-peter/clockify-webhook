package org.vari.clockify.webhook.accessdecision;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import com.google.cloud.functions.HttpRequest;

public interface AccessDecisionFilter {
    default boolean supports(HttpRequest request) {
        return true;
    }

    AccessDecision vote(HttpRequest request);

    public static boolean isOk(HttpRequest request, List<AccessDecisionFilter> filters, Consumer<String> logger,
            IntConsumer st, BiConsumer<Integer, String> st2) {
        for (AccessDecisionFilter filter : filters) {
            if (!filter.supports(request)) {
                continue;
            }
            AccessDecision ad = filter.vote(request);
            if (ad.vote >= 0) {
                continue;
            }

            logger.accept(ad.reason.logMessage);
            if (ad.reason.httpMessage == null) {
                st.accept(ad.reason.httpCode);
            } else {
                st2.accept(ad.reason.httpCode, ad.reason.httpMessage);
            }
            return false;
        }
        return true;
    }
}
