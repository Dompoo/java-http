package com.java.http.request_response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public record Body(
        byte[] value,
        ContentType contentType
) {
    public static final Body EMPTY = new Body(new byte[0], ContentType.PLAIN);

    public static Body plaintext(String data) {
        return new Body(data.getBytes(StandardCharsets.UTF_8), ContentType.PLAIN);
    }

    public static Body resource(String resourcePath) {
        byte[] value;
        try (final var inputStream = Body.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) throw new IllegalStateException("존재하지 않는 정적 리소스입니다. path=" + resourcePath);
            value = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new IllegalStateException("정적 리소스를 읽는 중 예외가 발생했습니다.", e);
        }
        return new Body(value, ContentType.from(resourcePath));
    }

    // TODO : 엣지케이스 처리
    public Map<String, String> asFormUrlEncoded() {
        String body = new String(value, StandardCharsets.UTF_8);
        return Arrays.stream(body.split("&"))
                .map(data -> data.split("="))
                .collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public int contentLength() {
        return this.value.length;
    }

    public byte[] toByteArray() {
        return this.value;
    }
}
