package com.java.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public record HttpRequest(
        StartLine startLine,
        Headers headers,
        Body body
) {
    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        List<String> startLineEndHeaders = readUntilNewLine(reader);
        StartLine startLine = StartLine.from(startLineEndHeaders.getFirst());
        Headers headers = Headers.from(startLineEndHeaders.subList(1, startLineEndHeaders.size() - 1));

        Body body = Body.EMPTY;
        String contentLength = headers.value("Content-Length");
        if (contentLength != null) {
            String bodyValue = readBy(reader, Integer.parseInt(contentLength));
            body = Body.plaintext(bodyValue);
        }

        return new HttpRequest(startLine, headers, body);
    }

    private static List<String> readUntilNewLine(BufferedReader reader) throws IOException {
        List<String> result = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            result.add(line);
            if (line.isEmpty()) {
                break;
            }
        }

        return result;
    }

    private static String readBy(BufferedReader reader, int contentLength) throws IOException {
        char[] bodyChars = new char[contentLength];
        int read = reader.read(bodyChars, 0, contentLength);
        return new String(bodyChars, 0, read);
    }

    public String param(String key) {
        return startLine.queryParameter().get(key);
    }

    // TODO : 단순 delegate 하는 형태... 올바른가?
    public HttpMethod method() {
        return startLine.method();
    }

    public String uri() {
        return startLine.uri();
    }

    public String cookie(String key) {
        return headers.cookies().get(key);
    }

    private record StartLine(
            HttpMethod method,
            String uri,
            Map<String, String> queryParameter
    ) {
        public static StartLine from(String startLine) {
            String[] requestLine = startLine.split(" ");

            if (!requestLine[1].contains("?")) {
                String method = requestLine[0];
                String uri = requestLine[1];
                return new StartLine(HttpMethod.parse(method), uri, Collections.emptyMap());
            } else {
                String method = requestLine[0];
                String[] uriAndParams = requestLine[1].split("\\?");
                String uri = uriAndParams[0];
                String params = uriAndParams[1];

                Map<String, String> paramMap = Arrays.stream(params.split("&"))
                        .map(param -> param.split("="))
                        .collect(Collectors.toMap(param -> param[0], param -> param[1]));
                return new StartLine(HttpMethod.parse(method), uri, Collections.unmodifiableMap(paramMap));
            }
        }
    }

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
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HTTP 메서드입니다. input=" + str));
        }
    }
}
