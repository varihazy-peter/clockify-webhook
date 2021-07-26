package org.vari.clockify.webhook.domain;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.Value;

@Value
public class ClockifyEvent {
    public final static String CLOCKIFY_EVENT_COLLECTION = "ClockifyEvents";
    public final static String TIME_ENTRY_COLLECTION = "TimeEntries";
    @NonNull
    Instant eventTime;
    @NonNull
    String id;
    @NonNull
    Map<String, Object> data;

    public final static String VALIDATED_AT_KEY = "__validatedAt";
    public final static String RECEIVED_AT_KEY = "__receivedAt";

    public Map<String, Object> timeEntryData() {
        Map<String, Object> ret = new HashMap<>(data.size() + 2);
        ret.putAll(data);
        ret.put(VALIDATED_AT_KEY, null); //
        ret.put(RECEIVED_AT_KEY, eventTime.toString()); //
        return ret;
    }
}
