package com.java.http.request_response;

public enum StatusCode {
    OK("OK", 200),
    FOUND("Found", 302),
    NOT_FOUND("Not Found", 404),
    INTERNAL_SERVER_ERROR("Internal Server Error", 500),
    ;

    final String codeName;
    final int codeNumber;

    StatusCode(String codeName, int codeNumber) {
        this.codeName = codeName;
        this.codeNumber = codeNumber;
    }
}
