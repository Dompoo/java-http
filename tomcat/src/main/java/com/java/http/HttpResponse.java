package com.java.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public record HttpResponse(
        String version,
        StatusCode statusCode,
        Headers headers,
        Body body
) {
    public static HttpResponseBuilder ok() {
        return new HttpResponseBuilder(200);
    }

    public static HttpResponseBuilder redirect(String location) {
        return new HttpResponseBuilder(302).location(location);
    }

    public static HttpResponseBuilder notFound(String message) {
        return new HttpResponseBuilder(404).plain(message);
    }

    public static HttpResponseBuilder internalServerError(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();
        return new HttpResponseBuilder(500).plain(stackTrace);
    }

    public static class HttpResponseBuilder {

        private final String version = "HTTP/1.1";
        private final StatusCode statusCode;
        private final Headers headers = new Headers();
        private Body responseBody = Body.EMPTY;

        public HttpResponseBuilder(int statusCode) {
            this.statusCode = StatusCode.parse(statusCode);
        }

        public HttpResponseBuilder plain(String data) {
            this.headers.contentType("text/plain;charset=utf-8");
            this.headers.contentLength(data.getBytes(StandardCharsets.UTF_8).length);
            this.responseBody = new Body(data);
            return this;
        }

        public HttpResponseBuilder html(String data) {
            this.headers.contentType("text/html;charset=utf-8");
            this.headers.contentLength(data.getBytes(StandardCharsets.UTF_8).length);
            this.responseBody = new Body(data);
            return this;
        }

        public HttpResponseBuilder html(byte[] data) {
            this.headers.contentType("text/html;charset=utf-8");
            this.headers.contentLength(data.length);
            this.responseBody = new Body(data);
            return this;
        }

        public HttpResponseBuilder css(byte[] data) {
            this.headers.contentType("text/css;charset=utf-8");
            this.headers.contentLength(data.length);
            this.responseBody = new Body(data);
            return this;
        }

        public HttpResponseBuilder js(byte[] data) {
            this.headers.contentType("application/javascript;charset=utf-8");
            this.headers.contentLength(data.length);
            this.responseBody = new Body(data);
            return this;
        }

        public HttpResponseBuilder icon(byte[] data) {
            this.headers.contentType("image/x-icon");
            this.headers.contentLength(data.length);
            this.responseBody = new Body(data);
            return this;
        }

        public HttpResponseBuilder location(String location) {
            this.headers.location(location);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(version, statusCode, headers, responseBody);
        }
    }

    private enum StatusCode {
        OK("OK", 200),
        REDIRECT("Found", 302),
        NOT_FOUND("Not Found", 404),
        INTERNAL_SERVER_ERROR("Internal Server Error", 500),
        ;

        final String codeName;
        final int codeNumber;

        StatusCode(String codeName, int codeNumber) {
            this.codeName = codeName;
            this.codeNumber = codeNumber;
        }

        public static StatusCode parse(int codeNumber) {
            return Arrays.stream(StatusCode.values())
                    .filter(value -> value.codeNumber == codeNumber)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상태코드입니다. input=" + codeNumber));
        }
    }

    public byte[] toByteArray() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            baos.write("%s %s %s\r\n".formatted(version, statusCode.codeNumber, statusCode.codeName).getBytes(StandardCharsets.UTF_8));
            baos.write(headers.toSimpleString().getBytes(StandardCharsets.UTF_8));
            baos.write("\r\n".getBytes(StandardCharsets.UTF_8));
            baos.write(body.value());
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("HTTP 응답을 구성하는 중에 예외가 발생했습니다.", e);
        }
    }
}
