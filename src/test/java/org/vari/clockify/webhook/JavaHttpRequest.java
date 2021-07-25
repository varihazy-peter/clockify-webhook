package org.vari.clockify.webhook;

import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.apache.http.client.utils.URLEncodedUtils;

@Getter(value = AccessLevel.NONE)
@Value
@Builder
public class JavaHttpRequest implements com.google.cloud.functions.HttpRequest {

    @NonNull
    String method;
    @NonNull
    java.net.URI uri;
    @NonNull
    Map<String, String> httpHeaders;
    @NonNull
    String body;

    @Override
    public Optional<String> getContentType() {
        return Optional.ofNullable(httpHeaders.get(org.apache.http.HttpHeaders.CONTENT_TYPE));
    }

    @Override
    public long getContentLength() {
        return body.length();
    }

    @Override
    public Optional<String> getCharacterEncoding() {
        return this.getContentType() //
                .filter(ct -> ct.contains(";")) //
                .map(ct -> ct.split(";", 2)) //
                .filter(a -> a.length == 2) //
                .map(a -> a[1]);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(body.getBytes());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return this.httpHeaders.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> List.of(e.getValue())));
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getUri() {
        return this.uri.toASCIIString();
    }

    @Override
    public String getPath() {
        return this.uri.getPath();
    }

    @Override
    public Optional<String> getQuery() {
        return Optional.ofNullable(this.uri.getQuery());
    }

    @Override
    public Map<String, List<String>> getQueryParameters() {
        return URLEncodedUtils.parse(this.uri, Charsets.UTF_8).stream()
                .collect(Collectors.groupingBy(org.apache.http.NameValuePair::getName,
                        Collectors.mapping(org.apache.http.NameValuePair::getName, Collectors.toList())));
    }

    @Override
    public Map<String, HttpPart> getParts() {
        throw new UnsupportedOperationException();
    }

}
