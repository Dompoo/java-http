package com.java.http;

import java.util.*;
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

    public void setCookie(String key, String value) {
        values.put("Set-Cookie", key + "=" + value);
    }

    public String value(String key) {
        return values.get(key);
    }

    public Map<String, String> cookies() {
        String data = values.get("Cookie");
        if (data == null) return Collections.emptyMap();
        return Arrays.stream(data.split(";"))
                .map(str -> {
                    int i = str.indexOf("=");
                    String cookieKey = str.substring(0, i).trim();
                    String cookieValue = str.substring(i + 1).trim();
                    return new String[]{cookieKey, cookieValue};
                })
                .collect(Collectors.toMap(str -> str[0], str -> str[1]));
    }

    public String toSimpleString() {
        StringBuilder sb = new StringBuilder();
        values.forEach((key, value) -> sb.append("%s: %s".formatted(key, value)).append("\r\n"));
        return sb.toString();
    }
}
