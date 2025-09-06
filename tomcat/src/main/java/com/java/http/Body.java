package com.java.http;

import java.nio.charset.StandardCharsets;

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
}
