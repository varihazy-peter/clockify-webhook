package org.vari.clockify.webhook;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteResult;
import io.vavr.control.Try;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.vari.clockify.webhook.domain.TimeEntryEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class Repo {
    FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder().build();

    Firestore firestore = firestoreOptions.getService();

    public WriteResult saveClockifyEvent(@NonNull TimeEntryEvent event) {
        CollectionReference coll = firestore.collection(TimeEntryEvent.CLOCKIFY_EVENT_COLLECTION);
        String dt = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
        String idd = event.getId() + "_" + dt + "_" + UUID.randomUUID();
        DocumentReference doc = coll.document(idd);
        WriteResult writeResult = Try.of(doc.set(event.getData())::get).get();
        log.info("ClockifyEvent id:{} created at {}, data: {}", idd, writeResult.getUpdateTime(), event.getData());
        log.info("ClockifyEvent id:{} created at {}", idd, writeResult.getUpdateTime());
        return writeResult;
    }

    public WriteResult saveTimeEntry(@NonNull TimeEntryEvent event) {
        CollectionReference coll = firestore.collection(TimeEntryEvent.TIME_ENTRY_COLLECTION);
        DocumentReference doc = coll.document(event.getId());
        WriteResult af = Try.of(doc.set(event.getData())::get).get();
        log.debug("TimeEntry id:{}, saved at: {}, data: {}", event.getId(), af.getUpdateTime(), event.getData());
        log.info("TimeEntry id:{}, saved at: {}", event.getId(), af.getUpdateTime());
        return af;
    }

}
