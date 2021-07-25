package org.vari.clockify.webhook;

import com.google.api.client.http.HttpMethods;
import java.net.URI;
import java.util.Map;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.FirestoreEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith(SystemStubsExtension.class)
@Testcontainers
public abstract class AbstractInterationTest {
    @Container
    public final static FirestoreEmulatorContainer FIRESTORE_EMULATOR = new FirestoreEmulatorContainer(
            DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:317.0.0-emulators"));
    public final static String uriString = "https://LOCATION-PROJECT_ID.cloudfunctions.net/clockify-webhook";
    public final static String CLOCKIFY_SIGNATURE = "verySecretSignature";
    @SystemStub
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables() //
            .set("GOOGLE_CLOUD_PROJECT", "test-project") //
            .set("CLOCKIFY_SIGNATURE", CLOCKIFY_SIGNATURE) //
            .set("FIRESTORE_EMULATOR_HOST", FIRESTORE_EMULATOR.getEmulatorEndpoint());

    public final Map<String, String> headerMap = Map.of( //
            "Clockify-Signature", CLOCKIFY_SIGNATURE, //
            "Content-Type", "application/json");

    public final JavaHttpRequest.JavaHttpRequestBuilder requestBuilder = JavaHttpRequest.builder() //
            .uri(URI.create(uriString)).method(HttpMethods.POST).httpHeaders(headerMap);
}
