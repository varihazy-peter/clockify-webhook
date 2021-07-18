package org.vari.clockify.webhook.accessdecision;

import com.google.cloud.functions.HttpRequest;

public class ContentTypeFilter implements AccessDecisionFilter {
    public static final String ALLOWED_CONTENT_TYPE = "application/json";

    @Override
    public AccessDecision vote(HttpRequest request) {
        String contentType = request.getContentType().orElse(null);
        if (contentType == null || !contentType.equalsIgnoreCase(ALLOWED_CONTENT_TYPE)) {
            String msg = "not acceptable; unknown ContentType " + (contentType == null ? "UNKNOWS" : contentType);
            return AccessDecision.failed(msg, java.net.HttpURLConnection.HTTP_NOT_ACCEPTABLE);
        }
        return AccessDecision.ok();
    }

}
