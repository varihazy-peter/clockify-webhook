package org.vari.clockify.webhook;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.vari.clockify.webhook.accessdecision.AccessControll;
import org.vari.clockify.webhook.domain.TimeEntryEvent;

public class ClockifyWebhook implements HttpFunction {
    private final AccessControll filters = new AccessControll();
    private final Repo repo = new Repo();
    private final TimeEntryEventParser parser = new TimeEntryEventParser();

    public void service(HttpRequest request, HttpResponse response) {
        if (filters.isFailed(request, response)) {
            return;
        }
        TimeEntryEvent event = parser.parse(request);
        if (event == null || event.getData() == null || event.getData().size() == 0) {
            response.setStatusCode(java.net.HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }
        this.repo.saveClockifyEvent(event);
        this.repo.saveTimeEntry(event);
        response.setStatusCode(java.net.HttpURLConnection.HTTP_OK);
    }
}
