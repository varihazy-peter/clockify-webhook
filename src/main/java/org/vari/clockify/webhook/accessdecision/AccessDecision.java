package org.vari.clockify.webhook.accessdecision;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
@RequiredArgsConstructor
public class AccessDecision {
    int vote;
    Reason reason;

    @FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
    @RequiredArgsConstructor
    public static class Reason {
        String logMessage;
        int httpCode;
        String httpMessage;
    }

    public static AccessDecision ok() {
        return new AccessDecision(1, null);
    }

    public static AccessDecision failed(String logMessage, int httpCode) {
        return failed(logMessage, httpCode, null);
    }

    public static AccessDecision failed(@NonNull String logMessage, int httpCode, String httpMessage) {
        return new AccessDecision(-1, new Reason(logMessage, httpCode, httpMessage));
    }
}
