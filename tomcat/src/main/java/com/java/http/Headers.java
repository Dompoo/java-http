package com.java.http;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public record Headers(
        Set<Header> values
) {
    public static final Headers EMPTY = new Headers(Collections.emptySet());

    public static Headers from(List<String> headers) {
        Set<Header> headerSet = headers.stream()
                .map(line -> {
                    int i = line.indexOf(":");
                    String key = line.substring(0, i).trim();
                    String value = line.substring(i + 1).trim();
                    return new Header(key, value);
                })
                .collect(Collectors.toSet());
        return new Headers(headerSet);
    }

    public Headers add(Header header) {
        Set<Header> newSet = new HashSet<>(values);
        newSet.add(header);
        return new Headers(newSet);
    }

    public String valueOf(String key) {
        return values.stream().filter(header -> header.key().equals(key))
                .map(Header::value)
                .findFirst()
                .orElse(null);
    }

    public Map<String, String> cookies() {
        String data = valueOf("Cookie");
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
        values.forEach((header) -> sb.append("%s: %s".formatted(header.key(), header.value())).append("\r\n"));
        return sb.toString();
    }

    public byte[] toByteArray() {
        return toSimpleString().getBytes(StandardCharsets.UTF_8);
    }
}
