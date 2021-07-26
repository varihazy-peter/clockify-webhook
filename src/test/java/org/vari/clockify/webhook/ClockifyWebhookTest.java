package org.vari.clockify.webhook;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vari.clockify.webhook.domain.ClockifyEvent;

@Slf4j
class ClockifyWebhookTest extends AbstractInterationTest {
    public final Gson gson = new GsonBuilder().create();
    public final java.lang.reflect.Type type = new TypeToken<Map<String, Object>>() {
    }.getType();

    @Test
    void test() throws IOException, InterruptedException, ExecutionException {
        String body = java.nio.file.Files.readString(Paths.get("src/test/resources/firestore/time_entry.json"));
        String id = "testId";
        Map<String, Object> originalDate = gson.fromJson(body, type);
        log.info("readed json: {}", originalDate);
        com.google.cloud.functions.HttpRequest httpRequest = requestBuilder.body(body).build();
        new ClockifyWebhook().service(httpRequest, Mockito.mock(com.google.cloud.functions.HttpResponse.class));
        Firestore firestore = FirestoreOptions.getDefaultInstance().toBuilder().build().getService();
        DocumentSnapshot document = firestore.collection(ClockifyEvent.TIME_ENTRY_COLLECTION).document(id).get().get();
        var map = new HashMap<>(document.getData());
        map.remove(ClockifyEvent.RECEIVED_AT_KEY);
        map.remove(ClockifyEvent.VALIDATED_AT_KEY);
        Assertions.assertEquals(originalDate, map);
        log.info("data: {}", map);
    }
}
