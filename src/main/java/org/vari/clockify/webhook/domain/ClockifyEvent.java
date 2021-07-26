package org.vari.clockify.webhook.domain;

import java.time.Instant;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

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
        return ImmutableMap.<String, Object> builderWithExpectedSize(data.size() + 2) //
                .putAll(data) //
                .put(VALIDATED_AT_KEY, null) //
                .put(RECEIVED_AT_KEY, Instant.now().toString()) //
                .build();
    }
}
