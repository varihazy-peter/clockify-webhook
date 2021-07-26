package org.vari.clockify.webhook;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.functions.HttpRequest;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.vari.clockify.webhook.domain.ClockifyEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TimeEntryEventParser {
    Gson gson = new GsonBuilder().create();
    java.lang.reflect.Type type = new TypeToken<Map<String, Object>>() {
    }.getType();

    public ClockifyEvent parse(HttpRequest request) throws ApiException {
        Map<String, Object> jsonMap = this.parseBody(request);
        String id = timeEntryId(jsonMap);
        return new ClockifyEvent(Instant.now(), id, jsonMap);
    }

    private Map<String, Object> parseBody(HttpRequest request) {
        try (BufferedReader reader = request.getReader()) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private final Pattern idPattern = Pattern.compile("[0-9a-zA-Z]+");

    private String timeEntryId(Map<String, Object> jsonMap) throws IllegalArgumentException {
        if (!jsonMap.containsKey("id")) {
            throw new IllegalArgumentException("no id field present");
        }
        Object ido = jsonMap.get("id");
        if (ido == null) {
            throw new IllegalArgumentException("the value of the id field is null");
        }
        if (!String.class.isInstance(ido)) {
            throw new IllegalArgumentException("the if field is not String");
        }
        String id = ((String) ido).strip();
        if (Strings.isNullOrEmpty(id)) {
            throw new IllegalArgumentException("no or empty id present");
        }
        if (!idPattern.matcher(id).matches()) {
            throw new IllegalStateException("invalid id present " + id);
        }
        return id;
    }

}
