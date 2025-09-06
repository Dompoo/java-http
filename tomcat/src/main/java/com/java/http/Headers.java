package com.java.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record Headers(
        Map<String, String> values
) {
    public Headers() {
        this(new HashMap<>());
    }

    public static Headers from(List<String> headers) {
        Map<String, String> values = headers.stream()
                .map(line -> {
                    int i = line.indexOf(":");
                    String key = line.substring(0, i).trim();
                    String value = line.substring(i + 1).trim();
                    return new String[]{key, value};
                })
                .collect(Collectors.toMap(value -> value[0], value -> value[1]));
        return new Headers(values);
    }

    public void contentType(String value) {
        values.put("Content-Type", value);
    }

    public void contentLength(int value) {
        values.put("Content-Length", String.valueOf(value));
    }

    public void location(String value) {
        values.put("Location", value);
    }

    public String value(String key) {
        return values.get(key);
    }

    public String toSimpleString() {
        StringBuilder sb = new StringBuilder();
        values.forEach((key, value) -> sb.append("%s: %s".formatted(key, value)).append("\r\n"));
        return sb.toString();
    }
}
