package org.vari.clockify.webhook.domain;

import java.time.Instant;
import java.util.Map;
import lombok.NonNull;
import lombok.Value;

@Value
public class TimeEntryEvent {
    public final static String CLOCKIFY_EVENT_COLLECTION = "ClockifyEvents";
    public final static String TIME_ENTRY_COLLECTION = "TimeEntries";
    @NonNull
    Instant eventTime;
    @NonNull
    String id;
    @NonNull
    Map<String, Object> data;
}
