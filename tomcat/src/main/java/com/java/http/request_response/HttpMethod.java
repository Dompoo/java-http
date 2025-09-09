package com.java.http.request_response;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    ;

    public static HttpMethod parse(String str) {
        return Arrays.stream(HttpMethod.values())
                .filter(value -> value.name().equalsIgnoreCase(str))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 HTTP 메서드입니다. input=" + str));
    }
}
