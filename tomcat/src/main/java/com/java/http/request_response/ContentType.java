package com.java.http.request_response;

public enum ContentType {
    PLAIN("text/plain;charset=utf-8"),
    HTML("text/html;charset=utf-8"),
    CSS("text/css;charset=utf-8"),
    JAVASCRIPT("application/javascript;charset=utf-8"),
    ICON("image/x-icon"),
    ;

    final String value;

    ContentType(String value) {
        this.value = value;
    }

    public static ContentType from(String resourcePath) {
        if (resourcePath.endsWith(".html")) return HTML;
        if (resourcePath.endsWith(".css")) return CSS;
        if (resourcePath.endsWith(".js")) return JAVASCRIPT;
        if (resourcePath.endsWith(".ico")) return ICON;
        else return PLAIN;
    }
}
