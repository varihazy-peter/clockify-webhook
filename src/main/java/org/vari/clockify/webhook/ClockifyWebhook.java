package org.vari.clockify.webhook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.vari.clockify.webhook.accessdecision.AccessDecisionFilter;
import org.vari.clockify.webhook.accessdecision.ClockifySignatureFilter;
import org.vari.clockify.webhook.accessdecision.ContentTypeFilter;
import org.vari.clockify.webhook.accessdecision.RateLimitFilter;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import io.vavr.control.Try;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClockifyWebhook implements HttpFunction {
    private final List<AccessDecisionFilter> filters = List.of( //
            new RateLimitFilter(),
            new ClockifySignatureFilter(),
            new ContentTypeFilter());
    FirestoreProvider firestoreProvider;
    private final Gson gson = new GsonBuilder().create();
    private final java.lang.reflect.Type type = new TypeToken<Map<String, Object>>() {
    }.getType();

    public void service(HttpRequest request, HttpResponse response) {
        if (!AccessDecisionFilter.isOk(request, filters, log::warn, response::setStatusCode, response::setStatusCode)) {
            return;
        }
        Map<String, Object> jsonMap = parse(request);
        if (jsonMap == null || jsonMap.size() == 0) {
            response.setStatusCode(java.net.HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }
        Optional<String> idO = timeEntryId(jsonMap);
        this.saveClockifyEvent(idO, jsonMap);
        idO.ifPresent(id -> this.saveTimeEntry(id, jsonMap));
        response.setStatusCode(java.net.HttpURLConnection.HTTP_OK);
    }

    private Map<String, Object> parse(HttpRequest request) {
        try (BufferedReader reader = request.getReader()) {
            return gson.fromJson(request.getReader(), type);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private final Pattern idPattern = Pattern.compile("[0-9a-zA-Z]+");

    private Optional<String> timeEntryId(Map<String, Object> jsonMap) {
        Object ido = jsonMap.get("id");
        if (!String.class.isInstance(ido)) {
            return Optional.empty();
        }
        String id = ido == null ? null : ((String) ido).strip();
        if (Strings.isNullOrEmpty(id)) {
            log.warn("no or empty id present");
            return Optional.empty();
        }
        if (!idPattern.matcher(id).matches()) {
            log.warn("invalid id present '{}'", id);
            return Optional.empty();
        }
        return Optional.ofNullable(id);
    }

    private WriteResult saveClockifyEvent(Optional<String> idO, Map<String, Object> jsonMap) {
        CollectionReference coll = firestoreProvider.get().collection("ClockifyEvents");
        String dt = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
        String id = idO.map(i -> i + "_" + dt + "_" + UUID.randomUUID()).orElse(null);
        DocumentReference doc = id == null ? coll.document() : coll.document(id);
        WriteResult af = Try.of(() -> doc.set(jsonMap).get()).get();
        log.info("ClockifyEvent saved at {}", af.getUpdateTime());
        return af;
    }

    private WriteResult saveTimeEntry(@NonNull String id, Map<String, Object> jsonMap) {
        CollectionReference coll = firestoreProvider.get().collection("TimeEntries");
        DocumentReference doc = coll.document(id);
        WriteResult af = Try.of(() -> doc.set(jsonMap).get()).get();
        log.info("TimeEntry saved at {}", af.getUpdateTime());
        return af;
    }
}
