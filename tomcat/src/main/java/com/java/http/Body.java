package com.java.http;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public record Body(
        byte[] value
) {
    public static final Body EMPTY = new Body(new byte[0]);

    public Body(byte[] value) {
        this.value = value;
    }

    public Body(String value) {
        this(value.getBytes(StandardCharsets.UTF_8));
    }

    public Map<String, String> asFormUrlEncoded() {
        String body = new String(value, StandardCharsets.UTF_8);
        return Arrays.stream(body.split("&"))
                .map(data -> data.split("="))
                .collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }
}
