package com.java.http;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public record Headers(
        Map<String, String> values
) {
    public static final Headers EMPTY = new Headers(Collections.emptyMap());
    
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

    public Headers setContentType(ContentType contentType) {
        Map<String, String> newMap = new HashMap<>(values);
        newMap.put("Content-Type", contentType.value);
        return new Headers(newMap);
    }

    public Headers setContentLength(int value) {
        Map<String, String> newMap = new HashMap<>(values);
        newMap.put("Content-Length", String.valueOf(value));
        return new Headers(newMap);
    }

    public Headers setLocation(String value) {
        Map<String, String> newMap = new HashMap<>(values);
        newMap.put("Location", value);
        return new Headers(newMap);
    }

    public Headers addCookie(String key, String value) {
        Map<String, String> newMap = new HashMap<>(values);
        newMap.put("Set-Cookie", key + "=" + value);
        return new Headers(newMap);
    }

    // TODO : 헤더는 대소문자 구분이 없단다...
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

    public byte[] toByteArray() {
        return toSimpleString().getBytes(StandardCharsets.UTF_8);
    }
}
