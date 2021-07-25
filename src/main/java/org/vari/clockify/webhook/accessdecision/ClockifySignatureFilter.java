package org.vari.clockify.webhook.accessdecision;

import com.google.cloud.functions.HttpRequest;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ClockifySignatureFilter implements AccessDecisionFilter {
    public static final String CLOCKIFY_SIGNATURE_HEADER_NAME = "Clockify-Signature";

    private static final Set<String> clockifySignatureSet = Arrays.stream(getEnv("CLOCKIFY_SIGNATURE").split(","))
            .collect(Collectors.toUnmodifiableSet());

    private static String getEnv(String name) {
        return Objects.requireNonNull(System.getenv(name), "no env " + name + " present");
    }

    @Override
    public AccessDecision vote(HttpRequest request) {
        String clockifySignature = request.getFirstHeader(CLOCKIFY_SIGNATURE_HEADER_NAME).orElse(null);
        if (clockifySignature == null) {
            return AccessDecision.failed("unauthorized; no " + CLOCKIFY_SIGNATURE_HEADER_NAME + " header present",
                    java.net.HttpURLConnection.HTTP_UNAUTHORIZED);
        }
        if (!clockifySignatureSet.contains(clockifySignature)) {
            return AccessDecision.failed("forbidden; unknown " + CLOCKIFY_SIGNATURE_HEADER_NAME,
                    java.net.HttpURLConnection.HTTP_FORBIDDEN);
        }
        return AccessDecision.ok();
    }

}
