package org.vari.clockify.webhook.accessdecision;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class AccessControll {
    private final List<AccessDecisionFilter> filters;

    public AccessControll() {
        this(List.of(new RateLimitFilter(), new ClockifySignatureFilter(), new ContentTypeFilter()));
    }

    public boolean isFailed(HttpRequest request, HttpResponse response) {
        for (AccessDecisionFilter filter : filters) {
            if (!filter.supports(request)) {
                log.info("request dos not supported by {}", filter.getClass());
                continue;
            }
            AccessDecision ad = filter.vote(request);
            if (ad.vote >= 0) {
                continue;
            }

            log.warn(ad.reason.logMessage);
            if (ad.reason.httpMessage == null) {
                response.setStatusCode(ad.reason.httpCode);
            } else {
                response.setStatusCode(ad.reason.httpCode, ad.reason.httpMessage);
            }
            return true;
        }
        return false;
    }
}
