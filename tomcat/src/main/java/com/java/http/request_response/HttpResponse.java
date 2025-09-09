package com.java.http.request_response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public record HttpResponse(
        String version,
        StatusCode statusCode,
        Headers headers,
        Body body
) {
    public static HttpResponse of(StatusCode statusCode) {
        return new HttpResponse("HTTP/1.1", statusCode, Headers.EMPTY, Body.EMPTY);
    }

    public static HttpResponse ok() {
        return HttpResponse.of(StatusCode.OK);
    }

    public static HttpResponse redirect(String location) {
        return HttpResponse.of(StatusCode.REDIRECT)
                .addHeader(Header.location(location));
    }

    public static HttpResponse notFound(String message) {
        return HttpResponse.of(StatusCode.NOT_FOUND)
                .body(Body.plaintext(message));
    }

    public static HttpResponse internalServerError(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();

        return HttpResponse.of(StatusCode.INTERNAL_SERVER_ERROR)
                .body(Body.plaintext(stackTrace));
    }

    public HttpResponse addHeader(Header header) {
        return new HttpResponse(this.version, this.statusCode, headers.add(header), this.body);
    }

    public HttpResponse body(Body body) {
        return new HttpResponse(
                this.version,
                this.statusCode,
                this.headers
                        .add(Header.contentType(body.contentType()))
                        .add(Header.contentLength(body.contentLength())),
                body
        );
    }

    public enum StatusCode {
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
    }

    public byte[] toByteArray() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            baos.write("%s %s %s\r\n".formatted(version, statusCode.codeNumber, statusCode.codeName).getBytes(StandardCharsets.UTF_8));
            baos.write(headers.toByteArray());
            baos.write("\r\n".getBytes(StandardCharsets.UTF_8));
            baos.write(body.toByteArray());
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("HTTP 응답을 구성하는 중에 예외가 발생했습니다.", e);
        }
    }
}
