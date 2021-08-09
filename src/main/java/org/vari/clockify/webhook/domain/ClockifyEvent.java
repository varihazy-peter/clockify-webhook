package org.vari.clockify.webhook.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
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

    public final static String VALIDATION_DATA_KEY = "validationData";

    public Map<String, Object> timeEntryData() {
        Map<String, Object> ret = new HashMap<>(data.size() + 1);
        Map<String, String> validationData = this.validationData();
        ret.putAll(data);
        ret.put(VALIDATION_DATA_KEY, validationData); //
        return ret;
    }

    /*
     * 
     * date: "2021-06-30" (string)
     * 
     * daySummaryDate: null (null)
     * 
     * receivedAt:"2021-08-09T12:48:13.697767Z" (string)
     * 
     * validatedAt: null
     */
    private Map<String, String> validationData() {
        Object timeInterval = this.data.get("timeInterval");
        if (!(timeInterval instanceof Map)) {
            throw new IllegalStateException("timeInterval must be Map");
        }
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Map<String, String> timeIntervalMap = (Map) timeInterval;
        if (!timeIntervalMap.containsKey("start")) {
            throw new IllegalStateException("Cannot find timeInterval.start");
        }
        String timeIntervalStart = timeIntervalMap.get("start");
        LocalDate date;
        try {
            date = OffsetDateTime.parse(timeIntervalStart).toLocalDate();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot parse timeInterval.start into LocalDate: " + timeIntervalStart, e);
        }
        Map<String, String> map = new HashMap<>(4);
        map.put("receivedAt", eventTime.toString());
        map.put("date", date.toString());
        map.put("daySummaryDate", null);
        map.put("validatedAt", null);
        return map;
    }
}
