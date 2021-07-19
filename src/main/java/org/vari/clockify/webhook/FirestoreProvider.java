package org.vari.clockify.webhook;

import java.util.Objects;
import java.util.function.Supplier;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

import io.vavr.Function0;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FirestoreProvider implements Supplier<Firestore> {
    String PROJECT_ID = getEnv("PROJECT_ID");
    GoogleCredentials credentials = Try.of(GoogleCredentials::getApplicationDefault).get();

    FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder().setProjectId(PROJECT_ID)
            .setCredentials(this.credentials).build();
    Supplier<Firestore> s = Function0.of(() -> firestoreOptions.getService()).memoized();

    private static String getEnv(String name) {
        return Objects.requireNonNull(System.getenv(name), "no env " + name + " present");
    }

    @Override
    public Firestore get() {
        return s.get();
    }
}
