package com.java.http.request_response;

public record Header(
        String key,
        String value
) {
    public static Header contentType(ContentType contentType) {
        return new Header("Content-Type", contentType.value);
    }

    public static Header contentLength(int value) {
        return new Header("Content-Length", String.valueOf(value));
    }

    public static Header location(String value) {
        return new Header("Location", value);
    }

    public static Header setCookie(String key, String value) {
        return new Header("Set-Cookie", key + "=" + value);
    }

    public boolean equalsIgnoreCase(String value) {
        return this.value.equalsIgnoreCase(value);
    }
}
